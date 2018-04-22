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
    [UnityEngine GameObject Debug Vector3]
    Control))

(defn car! [o]
  (let [body (child-named o "body")
        car (-clone! :car (>v3 body))]
    (parent! car body)))

(defn camera-update [o _]
  (when @PLAYER
    (let [cpos (.position (.transform o))]
      (set! (.position (.transform o))
        (Vector3/Lerp cpos 
          (v3 (.x (>v3 @PLAYER)) (.y cpos) (+ -14 (.z (>v3 @PLAYER)))) 0.1)))))

(defn entity-update [o _]
  (let [control (cmpt o Control)
        x (.h control)
        z (.v control)
        rb (->rigidbody o)
        v (.velocity rb)
        speed (if (and (= o @PLAYER) (:car @STATE)) 1.6 1.0)
        nv (v3+ (v3* (v3 x 0 z) (* 6 speed)) (v3 0  (.y v) 0))
        body (get (children o) 0)
        ]
    (set! (.velocity rb) nv)
    (param-float body "jogspeed" (* (.magnitude nv) 0.3))
    (if (> (.magnitude (v3 (.x nv) 0 (.z nv))) 0.02)
      (lerp-look! o (v3+ (>v3 o) (v3 (.x nv) 0 (.z nv))) 0.1))))

(defn player-update [o _]
  (if @DIALOGUE
    (let [control (cmpt o Control)]
      (set! (.h control) 0)
      (set! (.v control) 0))
    (let [control (cmpt o Control)
          x (get-axis :horizontal)
          z (get-axis :vertical)]
      (set! (.h control) x)
      (set! (.v control) z)
      (if (< (.y (>v3 o)) -100)
        (do-fn :load-area (or @AREA :areas/village) nil)))))

(defn make-player [pos]
  (let [o (-clone! :player pos)]
    (hook+ o :update :entity #'entity-update)
    (hook+ o :update #'player-update)
    (when (:car @STATE)
      (when-not (the indoors)
        (car! o)))
    o))


(defn make-animal 
  ([pos head] (make-animal pos head nil))
  ([pos head f]
    (let [o (-clone! :animal pos)
          head-mount (child-named o "head")
          head (-clone! head (>v3 head-mount))]
      (parent! head head-mount)
      (local-scale! head (v3 1))
      (hook+ o :update :entity #'entity-update)
      (when f
        (hook+ o :start f))
      o)))

(defn wander [o _]
  (let [control (cmpt o Control)]
    (timeline* :loop
      (wait (?f 0.5 8.0))
      (fn []
        (set! (.h control) (?f -0.6 0.6))
        (set! (.v control) (?f -0.6 0.6)) nil)
      (wait (?f 0.5 2.0))
      (fn []
        (set! (.h control) (float 0.0))
        (set! (.v control) (float 0.0)) nil))))



'(make-animal (v3) :heads/cat-head #'wander)