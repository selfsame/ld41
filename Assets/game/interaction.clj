(ns game.interaction
  (use
    arcadia.core
    arcadia.linear
    hard.core
    tween.core
    game.player
    game.data
    game.dialogue
    game.tips)
  (import
    [UnityEngine GameObject Debug Mathf]
    Portal
    Interaction))

(defonce ACTIONS (atom {}))

(defn register-action [k f]
  (swap! ACTIONS assoc k f))

(defmacro defaction [k args & code]
  `(register-action ~(if (keyword? k) (name k) (str k))
    (~'fn ~args ~@code)))

(defn action! [k c]
  (when-let [f (get @ACTIONS k)]
    (f c)))


(defaction landlord [c]
  (milestone :landlord/welcome
    (make-dialogue "LANDLORD" 
    "I see you've moved in...

    Remember rent is due at the end of the month, no rent - no apartment!" [
    ["ok" ] ])))

(defaction "areas/village" [c]
  (milestone :village/welcome
    (tip! "audio/hello-tippy" "frog-crazy")))