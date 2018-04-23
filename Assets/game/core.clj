(ns game.core
  (use
    arcadia.core
    arcadia.linear
    hard.core
    hard.input
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
  (let [-time (daytime)
        money (cmpt (child-named o "coins") UnityEngine.UI.Text)
        clock (cmpt (child-named o "time") UnityEngine.UI.Text)
        size (if (pos? @COINS) 0 (+ 0.6 (* 1.5 (abs (cos (* UnityEngine.Time/time 4))))))]
    (set! (.localScale (cmpt (the poor) UnityEngine.RectTransform)) (v3 size))
    (set! (.text money) (str @COINS))
    (set! (.text clock) (str 
      (get months @MONTH) " " @DAY "\n" (:hours -time) ":" (:minutes -time)
      (if (< (:hours -time) 13) "AM" "PM")))
    (when (> (:hours -time) 22)
      (action! "curfew" nil))))

(defn player-trigger-enter [o c _]
  (when-let [portal (cmpt (.gameObject c) Portal)]
    (when (.active portal)
      (load-area (.area portal) (.portal portal))))
  (when-let [action (cmpt (.gameObject c) Interaction)]
    (log action)
    (when (.active action)
      (action! (.key action) action))))

(defn player-trigger-exit [o c _]
  (when-let [portal (cmpt (.gameObject c) Portal)]
    (set! (.active portal) true)))

(defn load-area [area portal]
  (reset! DIALOGUE nil)
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
          (objects-tagged "spawn")))
      (action! area nil))))
(register-fn load-area :load-area)

(defn start [o _]
  (reset! DIALOGUE nil)
  (reset! TIME UnityEngine.Time/time)
  (reset! DAY 1)
  (reset! MONTH 1)
  (reset! COINS 100)
  (reset! STATE {
    :daybills {"food" 26}
    })
  (load-area "areas/town" nil))


(reset! DAYFN
  (fn []
    (let [bills (:daybills @STATE)
          total (reduce + (vals bills))]
      (timeline* 
        (fn [] @DIALOGUE)
        (fn [] 
          (if @DIALOGUE true
            (do 
          (swap! COINS #(- % total))
          (make-dialogue "TIME" 
            (str "Daily Bills:\n"
              (apply str (map #(str (first %) " - " (last %) "\n") bills))) 
            [["just another day."] ])
          nil)))))))

(reset! MONTHFN
  (fn []
    (swap! STATE assoc :finished true)
    (load-area :areas/game-over nil)
    (timeline*
      (wait 0.5)
      (tip! "audio/lose" "frog-lecture"))))


(defn intro [o _]
  (clear-cloned!)
  (let [intro (clone! :intro)]
    (hook+ intro :update
      (fn [o _]
        (if (key-down? "space")
          (start nil nil))))))


'(start nil nil)
'(hook+ (the hook) :start #'intro)

'(load-area :areas/factory nil)

