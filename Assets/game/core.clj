(ns game.core
  (use
    arcadia.core
    arcadia.linear
    hard.core
    tween.core
    game.player
    game.data)
  (import
    [UnityEngine GameObject Debug]
    Portal))

(declare load-area)

(defn player-trigger-enter [o c _]
  (when-let [portal (cmpt (.gameObject c) Portal)]
    (when (.active portal)
      (load-area (.area portal) (.portal portal)))))

(defn player-trigger-exit [o c _]
  (when-let [portal (cmpt (.gameObject c) Portal)]
    (set! (.active portal) true)))

(defn load-area [area portal]
  (clear-cloned!)
  (clone! :sun)
  (let [camera (clone! :village-camera)
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
    (let [cpos (>v3 camera)]
      (position! camera (v3 (.x (>v3 player)) (.y cpos) (.z cpos))))
    (reset! PLAYER player)
    (reset! AREA area)))
(register-fn load-area :load-area)

(defn start [o _]
  (load-area "areas/village" nil))


'(start nil nil)
'(hook+ (the hook) :start #'start)

