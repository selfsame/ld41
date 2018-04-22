(ns game.data
  (use
    arcadia.core))

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

(defmacro milestone [k & code]
  `(~'when-not (~'get ~'@STATE ~k)
     (~'swap! ~'STATE ~'assoc ~k true)
     ~@code))

(def months [
  "Farbuary"
  "Septober"
  "Augember"
  "Juln"
  "Necember"
  "Octember"
  "Marchember"
  "Janril"])

(def TIME (atom UnityEngine.Time/time))
(def DAY (atom 1))
(def MONTH (atom 1))
(def DAYFN (atom #(log "new day")))
(def MONTHFN (atom #(log "new  month")))


(defn daytime []
  (let [elapsed (- UnityEngine.Time/time @TIME)
        hours (/ elapsed 1 #_30 )
        minutes (mod hours 1)
        hours (int hours)
        minutes (int (* minutes 60))]
    (when (> hours 23)
      (swap! DAY inc)
      (reset! TIME UnityEngine.Time/time)
      (@DAYFN)
      (when (> @DAY 30)
        (reset! DAY 1)
        (swap! MONTH inc)
        (@MONTHFN)))
    {:hours hours :minutes minutes}))
