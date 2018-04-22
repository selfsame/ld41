(ns game.interaction
  (use
    arcadia.core
    arcadia.linear
    hard.core
    tween.core
    game.player
    game.data
    game.dialogue
    game.tips
    game.sudoku)
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

(defaction associate [c]
  (if-not (:car @STATE)
    (make-dialogue "SALES ASSOCIATE" 
  "  This is a 2014 Hondola Drivera with 148,000 miles.

  Would you like to buy it for a downpayment of 1,680???" [
  ["BUY CAR" 
    (fn [] (swap! COINS #(- % 1680))
      (car! @PLAYER)
      (swap! STATE assoc :car true)
      (swap! STATE update :daybills assoc "gas" 20)
      (destroy (the buy)))]
  ["no"] ])
    (make-dialogue "SALES ASSOCIATE" 
  "Back for a tune up?\n\n" [
  ["no"] ])))

(defaction carcheck [c]
  (if (:car @STATE)
    (destroy (the needcar))))

(defaction sleep [c]
  (next-day 8)
  (do-fn :load-area "areas/apartment" nil))

(defaction curfew [c]
  (next-day 14)
  (do-fn :load-area "areas/apartment" nil)
  (timeline* 
    (wait 0.2)
    (fn [] 
      (if @DIALOGUE true
        (do 
          (make-dialogue "TIME" 
          "Did you stay out all night?\n\n
          Somehow you made it home but you've slept in til after noon!!\n" [
          ["whatever"] ]) nil)))))

(defaction work [c]
  (new-game pattern))