(ns game.data
  (use
    arcadia.core
    tween.core)
  (require
    clojure.core.server))

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
  :heads/cat-head
  :heads/crow-head
  :heads/cat-head
  :heads/crow-head
  ;:heads/rent-seeker
  :heads/boy-head])

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

(def timescale 15)

(def TIME (atom UnityEngine.Time/time))
(def DAY (atom 1))
(def MONTH (atom 1))
(def DAYFN (atom #(log "new day")))
(def MONTHFN (atom #(log "new  month")))


(defn daytime []
  (let [elapsed (- UnityEngine.Time/time @TIME)
        hours (/ elapsed timescale )
        minutes (mod hours 1)
        hours (int hours)
        minutes (int (* minutes 60))]
    (when-not (:finished @STATE)
      (when (> hours 23)
        (swap! DAY inc)
        (reset! TIME UnityEngine.Time/time)
        (@DAYFN))
      (when (> @DAY 30)
          (reset! DAY 1)
          (swap! MONTH inc)
          (@MONTHFN)))
    {:hours hours :minutes minutes}))


(defn next-day [hours]
  (let [offset (* hours timescale)]
    (reset! TIME (- UnityEngine.Time/time offset))
    (swap! DAY inc)
    (timeline* 
      (wait 0.2)
      (fn [] (@DAYFN) nil))))