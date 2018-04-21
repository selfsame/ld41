(ns game.player
  (use
    arcadia.core
    arcadia.linear
    hard.core
    hard.input
    hard.physics
    hard.animation
    tween.core
    game.data)
  (import
    [UnityEngine GameObject Debug Vector3]))


(defn camera-update [o _]
  (when @PLAYER
    (let [cpos (.position (.transform o))]
      (set! (.position (.transform o))
        (Vector3/Lerp cpos 
          (v3 (.x (>v3 @PLAYER)) (.y cpos) (.z cpos)) 0.1)))))

(defn player-update [o _]
  (let [x (get-axis :horizontal)
        z (get-axis :vertical)
        rb (->rigidbody o)
        v (.velocity rb)
        nv (v3+ (v3* (v3 x 0 z) 6) (v3 0  (.y v) 0))
        body (get (children o) 0)]
    (set! (.velocity rb) nv)
    (param-float body "jogspeed" (* (.magnitude nv) 0.3))
    '(log (param-float body "jogspeed"))
    (if (< (.y (>v3 o)) -100)
      (do-fn :load-area (or @AREA :areas/village) nil))))

(defn make-player [pos]
  (let [o (-clone! :player pos)]
    (hook+ o :update #'player-update)
    o))