(ns game.tips
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
    [UnityEngine GameObject Debug Vector3 RectTransform]
    [Hard Helper]
    Amplitude
    Curve))

(defonce TIPPY (atom nil))


(defn euler-in-range [o lb ub r]
  (let [v (.localEulerAngles (.transform o))
        yangle (UnityEngine.Mathf/Lerp lb ub r)]
    (Vector3/Lerp v (v3 (.x v) yangle (.z v))  0.1)
    (v3 (.x v) yangle (.z v))))

(defn update-tippy [o _]
  (let [curve (.curve (cmpt o Curve))
        amplitude (* 5 (.amplitude (state o :amplitude)))
        amplitude (.Evaluate curve amplitude)
        top (state o :top)
        bottom (state o :bottom)]
    (set! (.localEulerAngles (.transform bottom))
          (euler-in-range bottom  -85 -21 amplitude))
    (set! (.localEulerAngles (.transform top))
          (euler-in-range top  -100 -154 amplitude))))

(defn setup-tippy []
  (let [cam (.gameObject (main-camera))
        mount (child-named cam "mount")
        tippy (clone! :tippy)]
    (state+ tippy :amplitude (cmpt tippy Amplitude))
    (state+ tippy :top (child-named tippy "Bone_004"))
    (state+ tippy :bottom (child-named tippy "Bone_005"))
    (hook+ tippy :late-update #'update-tippy)
    (parent! tippy mount)
    (local-position! tippy (v3 0 -5 0))
    (set! (.localRotation (.transform tippy)) (qt))
    (reset! TIPPY tippy)))

(defn play-clip [o s]
  (let [clip (UnityEngine.Resources/Load s)
        source (cmpt o UnityEngine.AudioSource)]
    (set! (.volume source) (float 2.0))
    (set! (.clip source) clip)
    (.Play source)
    (.length clip)))

(defn tip! [audio anim]
  (let [animation (cmpt @TIPPY UnityEngine.Animation)
        clip (if anim (Hard.Helper/GetAnimationState animation anim)
                (.clip animation))]
    (set! (.wrapMode animation) UnityEngine.WrapMode/Loop)
    (log (.clip clip))
    (set! (.clip animation) (.clip clip))
    ;(.AddMixingTransform clip (.transform (state @TIPPY :top)))
    ;(.AddMixingTransform clip (.transform (state @TIPPY :bottom)))
    (.Play animation)
    (timeline*
      (tween {:local {:position (v3 0 0.5 0)}} @TIPPY 0.8 {:out :pow3})
      (wait (play-clip (the tippy) audio))
      (tween {:local {:position (v3 0 -5 0)}} @TIPPY 0.8 {:out :pow3}))))


'(tip! "audio/hello-tippy" "frog-lecture")

'(setup-tippy)

'(play-clip (the tippy) "audio/hello-tippy")