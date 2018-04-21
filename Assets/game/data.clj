(ns game.data)

(defonce PLAYER (atom nil))
(defonce AREA (atom nil))

(defonce FNS (atom {}))

(defn register-fn [f k] (swap! FNS assoc k f))

(defn do-fn [k & args]
  (when-let [f (get @FNS k)] (apply f args)))