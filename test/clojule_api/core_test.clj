(ns clojule-api.core-test
  (:require [clojure.test :refer :all]
            [clojule-api.core :refer :all]))

; can't really test this properly, using random.
(deftest roll-die!-test
  (testing "Returns an integer"
    (is (int? (roll-die! 6)))))

; can't really test this properly, using random.
(deftest roll-dice!-test
  (testing "Returns integers"
    (is (every? int? (roll-dice! 2))))
  (testing "Returns the right number of die"
    (is (= (count (roll-dice! 6)) 6))))

(deftest split-distinct-test
  (testing "Splits into correct number of sets"
    (is (= (count (split-distinct '(1 1 2 2 3 3))) 3))
    (is (= (count (split-distinct '(1 1 1 1 3 3))) 2))
    (is (= (count (split-distinct '(1 1 1 1))) 1))
    (is (= (count (split-distinct '(1))) 1))))

(deftest straight?-test
  (testing "True when is a straight"
    (is (true? (straight? '(1 5 3 4 2 6)))))
  (testing "False when is not a straight"
    (is (false? (straight? '(1 1 3 4 2 6)))))
  (testing "False when is not a straight, not enough dice"
    (is (false? (straight? '(1 2 3 4 5))))))

(deftest two-triplets?-test
  (testing "True when is two triplets"
    (is (true? (two-triplets? '(1 1 1 2 2 2)))))
  (testing "False when is not two triplets"
    (is (false? (two-triplets? '(1 1 1 2 2 3)))))
  (testing "False when is not two triplets, not enough dice"
    (is (false? (two-triplets? '(1 1 1 2 2 ))))))

(deftest three-pairs?-test
  (testing "True when is three pairs"
    (is (true? (three-pairs? '(1 1 2 2 3 3)))))
  (testing "False when is not three pairs"
    (is (false? (three-pairs? '(1 1 2 2 3 4)))))
  (testing "False when is not three pairs, not enough dice"
    (is (false? (three-pairs? '(1 1 2 2 3))))))

(deftest six-of-kind?-test
  (testing "True when is a six of a kind - 1s"
    (is (= (six-of-kind? '(1 1 1 1 1 1)) [1 1 1 1 1 1])))
  (testing "True when is a six of a kind - 2s"
    (is (= (six-of-kind? '(2 2 2 2 2 2)) [2 2 2 2 2 2])))
  (testing "Nil when is not a six of a kind, not matching"
    (is (nil? (six-of-kind? '(1 1 3 4 2 6)))))
  (testing "Nil when is not a six of a kind, too few dice"
    (is (nil? (six-of-kind? '(1 1 1 1 1))))))

(deftest five-of-kind?-test
  (testing "True when is a five of a kind in six"
    (is (= (five-of-kind? '(1 1 1 1 1 2)) [1 1 1 1 1])))
  (testing "True when is a five of a kind in five"
    (is (= (five-of-kind? '(2 2 2 2 2)) [2 2 2 2 2])))
  (testing "Nil when is not a five of a kind, not matching"
    (is (nil? (five-of-kind? '(1 1 1 1 2 6)))))
  (testing "Nil when is not a five of a kind, too few dice"
    (is (nil? (five-of-kind? '(1 1 1 1))))))

(deftest four-of-kind?-test
  (testing "True when is a four of a kind in six"
    (is (= (four-of-kind? '(1 1 1 1 2 2)) [1 1 1 1])))
  (testing "True when is a four of a kind in four"
    (is (= (four-of-kind? '(2 2 2 2)) [2 2 2 2])))
  (testing "Nil when is not a four of a kind, not matching"
    (is (nil? (four-of-kind? '(1 1 1 3 2 6)))))
  (testing "Nil when is not a four of a kind, too few dice"
    (is (nil? (four-of-kind? '(1 1 1))))))

(deftest three-of-kind?-test
  (testing "True when is a three of a kind in six"
    (is (= (three-of-kind? '(1 1 1 3 2 2)) [1 1 1])))
  (testing "True when is a three of a kind in three"
    (is (= (three-of-kind? '(2 2 2)) [2 2 2])))
  (testing "False when is not a three of a kind, not matching"
    (is (nil? (three-of-kind? '(1 1 3 3 2 6)))))
  (testing "False when is not a three of a kind, too few dice"
    (is (nil? (three-of-kind? '(1 1))))))

(deftest score-six-dice-test
  (testing "6 dice score correctly"
    (is (= (:score (score-six-dice {:dice '(1 1 1 1 1 3) :score 0})) 0))
    (is (= (:score (score-six-dice {:dice '(1 1 1 1 1 1) :score 0})) 3000))))

(deftest score-dice-test
  (testing "6 dice score correctly"
    (is (= (:score (score-dice {:dice '(1 1 1 1 1 1) :score 0})) 3000))
    (is (= (:score (score-dice {:dice '(1 1 1 2 2 2) :score 0})) 2500))
    (is (= (:score (score-dice {:dice '(1 1 2 2 3 3) :score 0})) 1500))
    (is (= (:score (score-dice {:dice '(1 2 3 4 5 6) :score 0})) 1500)))
  (testing "5 dice score correctly"
    (is (= (:score (score-dice {:dice '(1 1 1 1 1) :score 0})) 2000))
    (is (= (:score (score-dice {:dice '(2 2 2 2 2) :score 0})) 2000))
    (is (= (:score (score-dice {:dice '(1 1 1 1 1 3) :score 0})) 2000)))
  (testing "4 dice score correctly"
    (is (= (:score (score-dice {:dice '(1 1 1 1 2) :score 0})) 1000))
    (is (= (:score (score-dice {:dice '(2 2 2 2) :score 0})) 1000))
    (is (= (:score (score-dice {:dice '(1 1 1 1 2 3) :score 0})) 1000)))
  (testing "3 dice score correctly"
    (is (= (:score (score-dice {:dice '(1 1 1 2 2) :score 0})) 300))
    (is (= (:score (score-dice {:dice '(2 2 2 3) :score 0})) 200))
    (is (= (:score (score-dice {:dice '(3 3 4 4 2 3) :score 0})) 300))
    (is (= (:score (score-dice {:dice '(4 4 4) :score 0})) 400))
    (is (= (:score (score-dice {:dice '(5 5 5 6) :score 0})) 500))
    (is (= (:score (score-dice {:dice '(6 6 6 2 2) :score 0})) 600)))
  (testing "2 dice score correctly"
    (is (= (:score (score-dice {:dice '(1 2 3 4 2 5) :score 0})) 150))
    (is (= (:score (score-dice {:dice '(1 2 3 4 2) :score 0})) 100))
    (is (= (:score (score-dice {:dice '(1 1) :score 0})) 200))
    (is (= (:score (score-dice {:dice '(5 5) :score 0})) 100))
    (is (= (:score (score-dice {:dice '(1 5) :score 0})) 150))
    (is (= (:score (score-dice {:dice '(5 5 5 6) :score 0})) 500))
    (is (= (:score (score-dice {:dice '(6 6 6 2 2) :score 0})) 600)))
  (testing "1 die scores correctly"
    (is (= (:score (score-dice {:dice '(1 2) :score 0})) 100))
    (is (= (:score (score-dice {:dice '(5 3) :score 0})) 50))
    (is (= (:score (score-dice {:dice '(1) :score 0})) 100))
    (is (= (:score (score-dice {:dice '(5) :score 0})) 50)))
  (testing "Mixtures of dice score correctly"
    (is (= (:score (score-dice {:dice '(3 3 3 3 1 1) :score 0})) 1200))
    (is (= (:score (score-dice {:dice '(3 3 3 5 1 1) :score 0})) 550))
    (is (= (:score (score-dice {:dice '(3 4 5 5 1 1) :score 0})) 300))
    (is (= (:score (score-dice {:dice '(3 1 1) :score 0})) 200))
    (is (= (:score (score-dice {:dice '(3 4 5 1) :score 0})) 150))
    (is (= (:score (score-dice {:dice '(4 5 5 6 2 1) :score 0})) 200))
    (is (= (:score (score-dice {:dice '(4 6 3 6 3 2) :score 0})) 0))
    (is (= (:score (score-dice {:dice '(4) :score 0})) 0))
    ))

(deftest roll-again?-test
  (testing "Always roll again if there are 6 dice."
    (is (true? (roll-again? {:score 0 :dice '(1 1 1 1 1 1) :max-score 0}))))
  (testing "If another player is going to win this round, roll again, unless the max score is mine."
    (is (true? (roll-again? {:score 0 :dice '(2 3) :max-score 10000})))
    (is (true? (roll-again? {:score 0 :dice '(2 3) :max-score 10001})))
    (is (false? (roll-again? {:score 10001 :dice '(2 3) :max-score 10001})))))
  (testing "If I have 3 or dice left, roll again."
    (is (true? (roll-again? {:score 0 :dice '(2 3 4) :max-score 0})))
    (is (true? (roll-again? {:score 0 :dice '(2 2 3 4) :max-score 0})))
    (is (false? (roll-again? {:score 0 :dice '(2 2) :max-score 0}))))
