(ns game.dialogue
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
    [UnityEngine GameObject Debug Vector3 RectTransform]))

(defn ui-parent! [a b]
  (.SetParent (cmpt a RectTransform) (cmpt b RectTransform) false))

(defn clear-dialogue []
  (when @DIALOGUE
    (destroy @DIALOGUE))
  (reset! DIALOGUE nil))

(defn update-dialogue [o _]
  (try 
    (let []
      (dorun (map-indexed
        (fn [i option]
          (if (= (state o :active) i)
            (set! (.color (cmpt option UnityEngine.UI.Text)) (color 1 0 0))
            (set! (.color (cmpt option UnityEngine.UI.Text)) (color 1 1 0))))
        (state o :options)))
      (cond 
        (key-down? "s")
        (state+ o :active 
          (if (< (state o :active) (dec (count (state o :options))))
              (inc (state o :active))
              0))
        (key-down? "w")
        (state+ o :active 
          (if (> (state o :active) 0)
              (dec (state o :active))
              (dec (count (state o :options)))))
        (or (key-down? "return")(key-down? "e"))
        (let [option (get (state o :options) (state o :active))]
          (let [f (state option :f)]
            (clear-dialogue)
            (f)))))
    (catch Exception e)))

(defn make-dialogue [who story opts]
  (when @DIALOGUE
    (destroy-immediate @DIALOGUE))
  (let [canvas (the Canvas)
        dialogue (clone! :ui/dialogue)]
    (ui-parent! dialogue canvas)
    (reset! DIALOGUE dialogue)
    (set! (.text (cmpt (child-named dialogue "who") UnityEngine.UI.Text)) who)
    (set! (.text (cmpt (child-named dialogue "story") UnityEngine.UI.Text)) story)
    (state+ dialogue :active 0)
    (hook+ dialogue :update #'update-dialogue)
    (state+ dialogue :options 
      (vec
        (map-indexed 
          (fn [i [s f]]
            (let [option (clone! :ui/option)]
              (ui-parent! option dialogue)
              (set! (.text (cmpt option UnityEngine.UI.Text)) s)
              (set! (.position (cmpt option RectTransform))
                (v3+ (.position (cmpt option RectTransform))
                     (v3* (v3 0 -10 0) i)))
              (state+ option :f f)
              option))
          opts)))))
