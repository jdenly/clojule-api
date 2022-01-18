(ns clojule-api.core)

(defn roll-die! [sides] (+ (rand-int (- sides 1)) 1))

(defn roll-dice! [num] (repeatedly num #(roll-die! 6)))

(defn split-distinct [dice]
  "Split given dice sequence into separate sequences based on distinct numbers."
  (map (fn [x] (get (group-by #(= x %1) dice) true)) (distinct dice)))

(defn n-of-kind? [dice n]
  "Given dice sequence, find n of kind and return first one found. If none found, returns nil."
  (->> (split-distinct dice)
       (map #(if (= n (count %)) %))
       (remove nil?)
       (first)))

(defn straight? [dice] (= (sort dice) '(1 2 3 4 5 6)))

(defn two-triplets? [dice] (and (= 6 (count dice)) (every? #(= 3 (count %)) (split-distinct dice))))

(defn three-pairs? [dice] (and (= 6 (count dice)) (every? #(= 2 (count %)) (split-distinct dice))))

(defn six-of-kind? [dice] (n-of-kind? dice 6))

(defn five-of-kind? [dice] (n-of-kind? dice 5))

(defn four-of-kind? [dice] (n-of-kind? dice 4))

(defn three-of-kind? [dice] (n-of-kind? dice 3))

(defn score-six-dice [sm]
  ; only check for score if there are at least 6 dice left
  (if (>= (count(:dice sm)) 6)
    ; check for score, if there isn't any pass through existing score/dice
    (cond
      (= (count (six-of-kind? (:dice sm))) 6) {:score 3000 :dice []}
      (two-triplets? (:dice sm)) {:score 2500 :dice []}
      (three-pairs? (:dice sm)) {:score 1500 :dice []}
      (straight? (:dice sm)) {:score 1500 :dice []}
      :else sm
      )
    ; not enough dice to score any 6 combos, pass through
    sm))

(defn score-five-dice [sm]
  ; only check for score if there are at least 5 dice left
  (if (>= (count (:dice sm)) 5)
    ; check for score, if there isn't any pass through existing score/dice
    (let [f (five-of-kind? (:dice sm))]
      (cond
        (= (count f) 5) {:score (+ (:score sm) 2000) :dice (vec (remove #(= (first f) %) (:dice sm)))}
        :else sm))
    ; not enough dice to score five of a kind, pass through
    sm))

(defn score-four-dice [sm]
  ; only check for score if there are at least 4 dice left
  (if (>= (count (:dice sm)) 4)
    ; check for score, if there isn't any pass through existing score/dice
    (let [f (four-of-kind? (:dice sm))]
      (cond
        (= (count f) 4) {:score (+ (:score sm) 1000) :dice (vec (remove #(= (first f) %) (:dice sm)))}
        :else sm))
    ; not enough dice to score four of a kind, pass through
    sm))

(defn- score-three? [f x] (and (= (count f) 3) (= (first f) x)))

(defn- three-score [sm f x] {:score (+ (:score sm) x) :dice (vec (remove #(= (first f) %) (:dice sm)))})

(defn score-three-dice [sm]
  ; only check for score if there are at least 3 dice left
  (if (>= (count (:dice sm)) 3)
    ; check for score, if there isn't any pass through existing score/dice
    (let [f (three-of-kind? (:dice sm))]
      (cond
        (score-three? f 6) (three-score sm f 600)
        (score-three? f 5) (three-score sm f 500)
        (score-three? f 4) (three-score sm f 400)
        (score-three? f 3) (three-score sm f 300)
        (score-three? f 2) (three-score sm f 200)
        (score-three? f 1) (three-score sm f 300)
        :else sm))
    ; not enough dice to score three of a kind, pass through
    sm))

(defn score-ones-and-fives [sm]
  ; only check for score if there is at least 1 die left
  (if (>= (count(get sm :dice)) 1)
    ; remove the scoring dice and increment the score (100 for a single 1, 50 for a single 5)
    {:dice (vec (remove #(or (= % 1) (= % 5)) (:dice sm)))
     :score (+
              (:score sm)
              (reduce + (map (fn [x] (cond
                                       (= x 1) 100
                                       (= x 5) 50
                                       :else 0)) (:dice sm))))}
    ; not enough die left, pass through the existing score/dice
    sm))

; Scoring rules
; 6 dice
; 6 of a kind - 3000
; 2 triplets  - 2500
; 3 pairs     - 1500
; straight    - 1500

; 5>= dice
; 5 of a kind - 2000

; >=4 dice
; 4 of a kind - 1000

; >=3 dice
; 3 x 6       - 600
; 3 x 5       - 500
; 3 x 4       - 400
; 3 x 3       - 300
; 3 x 2       - 200
; 3 x 1       - 300

; n dice
; 1           - 100
; 5           - 50
(defn score-dice [sm]
  (-> (score-six-dice sm)
      score-five-dice
      score-four-dice
      score-three-dice
      score-ones-and-fives))

(defn roll-again? [d]
  "Should the AI roll again? Takes into account remaining dice, score and score gap with others."
  (cond
    (> (count (:dice d)) 2) true
    (and (> (:max-score d) 9999) (not= (:max-score d) (:score d))) true
    :else false))
