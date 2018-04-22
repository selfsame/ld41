(ns game.core
  (use
    arcadia.core
    arcadia.linear
    hard.core
    tween.core
    game.player
    game.data
    game.dialogue
    game.tips
    game.interaction)
  (import
    [UnityEngine GameObject Debug Mathf]
    Portal
    Interaction))

(declare load-area)

(defmacro defer [& code]
  `(~'timeline*
     ~'(wait 0.1)
     (~'fn []
        ~@code
        nil)))


(defn update-ui [o _]
  (let [money (cmpt (child-named o "coins") UnityEngine.UI.Text)
        size (if (pos? @COINS) 0 (+ 0.6 (* 1.5 (abs (cos (* UnityEngine.Time/time 4))))))]
    (set! (.localScale (cmpt (the poor) UnityEngine.RectTransform)) (v3 size))
    (set! (.text money) (str @COINS))))

(defn player-trigger-enter [o c _]
  (when-let [portal (cmpt (.gameObject c) Portal)]
    (when (.active portal)
      (load-area (.area portal) (.portal portal))))
  (when-let [action (cmpt (.gameObject c) Interaction)]
    (when (.active action)
      (action! (.key action) action))))

(defn player-trigger-exit [o c _]
  (when-let [portal (cmpt (.gameObject c) Portal)]
    (set! (.active portal) true)))

(defn load-area [area portal]
  (clear-cloned!)
  (defer
    (clone! :sun)
    (clone! :EventSystem)
    (let [camera (clone! :village-camera)
          canvas (clone! :Canvas)
          a (clone! area)
          p (child-named a portal)
          ploc (if p (>v3 p) (v3))
          player (make-player ploc)]
      (if p
        (when-let [portal (cmpt p Portal)]
          (set! (.active portal) false)))
      (hook+ player :on-trigger-enter #'player-trigger-enter)
      (hook+ player :on-trigger-exit #'player-trigger-exit)
      (hook+ camera :update #'game.player/camera-update)
      (hook+ canvas :update #'update-ui)
      (setup-tippy)
      (let [cpos (>v3 camera)]
        (position! camera (v3 (.x (>v3 player)) (.y cpos) (.z cpos))))
      (reset! PLAYER player)
      (reset! AREA area)
      (dorun
        (map 
          (fn [s]
            (make-animal (>v3 s) (rand-nth animal-heads) #'game.player/wander))
          (objects-tagged "spawn"))))))
(register-fn load-area :load-area)

(defn start [o _]
  (reset! COINS 100)
  (reset! STATE {})
  (load-area "areas/village" nil))







'(make-dialogue "SALES ASSOCIATE" 
  "  This is a 2014 Hondola Drivera with 148,000 miles.

  Would you like to buy it for a downpayment of 4,680???" [
  ["BUY CAR" 
    (fn [] (swap! COINS #(- % 4680))
      (car! @PLAYER)
      (swap! STATE assoc :car true))]
  ["no"] ])

'(start nil nil)
'(hook+ (the hook) :start #'start)

