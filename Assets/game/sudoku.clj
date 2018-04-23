(ns game.sudoku
  (use
    arcadia.core
    arcadia.linear
    hard.core
    hard.input
    hard.physics
    hard.animation
    tween.core
    game.data
    game.tips
    game.dialogue)
  (import
    [UnityEngine GameObject Debug Vector3 RectTransform]))

(declare win-sudoku)

(defmacro defer- [& code]
  `(~'timeline*
     ~'(wait 0.1)
     (~'fn []
        ~@code
        nil)))

(def pattern [
  [2 8 5 0 0 6 9 0 0]
  [3 0 0 0 9 0 8 5 0]
  [7 6 9 0 0 0 2 0 0]
  [0 3 0 4 2 0 0 6 5]
  [0 2 0 1 0 0 0 8 9]
  [8 0 0 0 0 0 4 0 2]
  [1 0 3 0 5 0 0 0 0]
  [0 0 0 0 0 3 0 0 0]
  [6 0 0 2 1 9 5 4 0]])

(defn shuffle-pattern [pat]
  (if (< (rand) 0.5)
    (vec (reverse pat))
    (mapv #(vec (reverse %)) pat)))


(def digits #{1 2 3 4 5 6 7 8 9})

(defn check-solved [pat]
  (if (every? true? (mapv (fn [row] (= digits (set row))) pat))
    (win-sudoku)))

(defn click-tile [o _]
  (log (state o :n))
  (state+ (the board) :active o)
  (position! (the circle) (>v3 o)))

(defn board-update [o _]
  (when-let [active (state o :active)]
    (let [n (cond 
      (key-down? "1") 1
      (key-down? "2") 2
      (key-down? "3") 3
      (key-down? "4") 4
      (key-down? "5") 5
      (key-down? "6") 6
      (key-down? "7") 7
      (key-down? "8") 8
      (key-down? "9") 9
      :else nil)]
    (when n
      (let [[x y] (state active :pos)]
        (state+ active :n n)
        (text! (first (children active)) (str n))
        (swap! (state o :pattern) assoc-in [y x] n)
        (check-solved @(state o :pattern))))))
  (when (> (:hours (daytime)) 17)
    (do-fn :load-area "areas/factory" nil)
    (timeline* 
      (wait 0.2)
      (fn [] 
        (if @DIALOGUE true
          (do 
            (make-dialogue "TIME" 
            "Time to clock out!\n\nAnother day another moneycoin..." [
            ["whew"] ]) nil))))))


(defn new-game [pat]
  (defer- 
    (reset! DIALOGUE true)
    (clear-cloned!)
    (clone! :sun)
    (clone! :sudoku-cam)
    (clone! :circle)
    (clone! :sudoku-canvas)
    (clone! :sudoku-background)
    (setup-tippy)
    (let [board (clone! :board)]
      (state+ board :pattern (atom pat))
      (hook+ board :update #'board-update)
      (dorun
        (for [x (range 9)
              y (range 9)
              :let [n (get (get pat y) x)
                    solved (not= 0 n)
                    n (if solved n "")
                    fab (if solved :square-gray :square-blue)
                    tile (-clone! fab (v3- (v3* (v3 x 0 y) 2) (v3 8 0 8)))]]
          (do 
            (state+ tile :pos [x y])
            (state+ tile :n (get (get pat y) x))
            (if-not solved 
              (hook+ tile :on-mouse-down #'click-tile))

            (text! (first (children tile)) (str n))
            (parent! tile board))))
      (position! board (v3 30 0 0))
      (timeline*
        (tween {:position (v3 0 0 0)} board 1.0 {:in :pow2 :out :pow2})))))

(defn win-sudoku []
  (swap! COINS #(+ % 80))
  (tip! (rand-nth ["audio/sudoku2" "audio/sudoku1"]) "frog-crazy")
  (timeline*
    (wait 3.0)
    (tween {:position (v3 -30 0 0)} (the board) 1.0 {:in :pow2 :out :pow2})
    (fn [] 
      (new-game pattern) nil)))

'(new-game pattern)
'(win-sudoku)