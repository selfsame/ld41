(ns game.data)

(defonce PLAYER (atom nil))
(defonce AREA (atom nil))
(defonce COINS (atom 100))
(defonce DIALOGUE (atom false))
(defonce STATE (atom {}))

(defonce FNS (atom {}))

(defn register-fn [f k] (swap! FNS assoc k f))

(defn do-fn [k & args]
  (when-let [f (get @FNS k)] (apply f args)))

(def animal-heads [
  :heads/cat-head])