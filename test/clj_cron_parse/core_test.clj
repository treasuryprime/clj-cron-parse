(ns clj-cron-parse.core-test
  (:require
   [clj-cron-parse.core :refer :all]
   [clj-time.core :as t]
   [clojure.test :refer [deftest is testing]]
   [midje.sweet :refer :all]))

(defchecker date [& date-args]
  (checker [actual]
           (let [d (apply t/date-time date-args)]
             (= d actual))))

(def now (t/date-time 2015 01 01 12 00 00 000))
(def nye (t/date-time 2014 12 31 12 00 00 000))

(time (facts "should find next date for cron expression"
             (next-date now "1 * * * * *") => (date 2015 01 01 12 00 01 000)
             (next-date (t/date-time 2022 05 24 10 59 00) "0 */5 * * * *") => (t/date-time 2022 05 24 11 00 00)
             (next-date now "* 1 * * * *") => (date 2015 01 01 12 01 00 000)
             (next-date now "* * 13 * * *") => (date 2015 01 01 13 00 00 000)
             (next-date now "* * * 10 * *") => (date 2015 01 10 12 00 00 000)
             (next-date now "* * * * 2 *") => (date 2015 02 01 12 00 00 000)
             (next-date now "11 12 13 14 10 *") => (date 2015 10 14 13 12 11 000)
             (next-date now "* * * * * 0") => (date 2015 01 04 12 00 00 000)
             (next-date now "* * * * * 3") => (date 2015 01 07 12 00 00 000)
             (next-date nye "* * 10 * * *") => (date 2015 01 01 10 00 00 000)
             (next-date now "1,2 * * * * *") => (date 2015 01 01 12 00 01 000)
             (next-date now "1-20 * * * * *") => (date 2015 01 01 12 00 01 000)
             (next-date now "*/2 * * * * *") => (date 2015 01 01 12 00 02 000)
             (next-date now "1-20/2 * * * * *") => (date 2015 01 01 12 00 01 000)
             (next-date (t/date-time 2015 01 01 12 00 04 000) "3-20/2 * * * * *") => (date 2015 01 01 12 00 05 000)
             (next-date now "* 1,2 * * * *") => (date 2015 01 01 12 01 00 000)
             (next-date now "* */2 * * * *") => (date 2015 01 01 12 00 00 000)
             (next-date now "* 1-20 * * * *") => (date 2015 01 01 12 01 00 000)
             (next-date now "* 1-20/2 * * * *") => (date 2015 01 01 12 01 00 000)
             (next-date (t/date-time 2015 01 01 12 05 00 000) "* 1-20/3 * * * *") => (date 2015 01 01 12 07 00 000)
             (next-date now "* * 1,2 * * *") => (date 2015 01 02 01 00 00 000)
             (next-date (t/date-time 2015 01 01 13 00 00 000) "* * */2 * * *") => (date 2015 01 01 14 00 00 000)
             (next-date (t/date-time 2015 01 01 21 00 00 000) "* * 1-20 * * *") => (date 2015 01 02 01 00 00 000)
             (next-date now "* * 1-20/2 * * *") => (date 2015 01 01 13 00 00 000)
             (next-date now "* * 11 1,2 * *") => (date 2015 01 02 11 00 00 000)
             (next-date now "* * * */2 * *") => (date 2015 01 02 12 00 00 000)
             (next-date now "* * 11 1-20 * *") => (date 2015 01 02 11 00 00 000)
             (next-date now "* * 11 1-20/2 * *") => (date 2015 01 03 11 00 00 000)
             (next-date now "* * * L * *") => (date 2015 01 31 12 00 00 000)
             (next-date now "* * * W * *") => (date 2015 01 02 12 00 00 000)
             (next-date (t/date-time 2015 01 02 12 00 00 000) "* * * W * *") => (date 2015 01 05 12 00 00 000)
             (next-date now "* * * * FEB *") => (date 2015 02 01 12 00 00 000)
             (next-date now "* * * * feb *") => (date 2015 02 01 12 00 00 000)
             (next-date now "* * * * 2 *") => (date 2015 02 01 12 00 00 000)
             (next-date now "* * * * 2,3 *") => (date 2015 02 01 12 00 00 000)
             (next-date now "* * * * */2 *") => (date 2015 02 01 12 00 00 000)
             (next-date now "* * * * 2-11 *") => (date 2015 02 01 12 00 00 000)
             (next-date now "* * * * 2-11/3 *") => (date 2015 02 01 12 00 00 000)
             (next-date (t/date-time 2015 03 01 12 00 00 000) "* * * * 1-11/3 *") => (date 2015 04 01 12 00 00 000)
             (next-date now "* * * * * 1,2") => (date 2015 01 05 12 00 00 000)
             (next-date now "* * 11 * * 1-5") => (date 2015 01 02 11 00 00 000)
             (next-date now "* * 11 * * W") => (date 2015 01 02 11 00 00 000)
             (next-date now "* * * * * MON") => (date 2015 01 05 12 00 00 000)
             (next-date now "* * * * * mon") => (date 2015 01 05 12 00 00 000)
             (next-date now "* * * * * */2") => (date 2015 01 02 12 00 00 000)
       ;(next-date now "* * * * * 1-5/2") => (bit of an edge case I think)
             (next-date now "* * * * * 1L") => (date 2015 01 26 12 00 00 000)
             (next-date now "* * * * * 1L,2L") => (date 2015 01 26 12 00 00 000)
             (next-date now "* * * * * 2L") => (date 2015 01 27 12 00 00 000)
             (next-date now "* * * * * 3L") => (date 2015 01 28 12 00 00 000)
             (next-date now "* * * * * 4L") => (date 2015 01 29 12 00 00 000)
             (next-date now "* * * * * 5L") => (date 2015 01 30 12 00 00 000)
             (next-date now "* * * * * 6L") => (date 2015 01 31 12 00 00 000)
             (next-date now "* * * * * 7L") => (date 2015 01 25 12 00 00 000)
             (next-date now "* * * * * 6L,7L") => (date 2015 01 25 12 00 00 000)
             (next-date (t/date-time 2015 01 02 12 00 00 000) "* * 11 * * 1-5") => (date 2015 01 05 11 00 00 000)
             (next-date (t/date-time 2015 01 02 12 00 00 000) "* * 11 * * W") => (date 2015 01 05 11 00 00 000)
             (next-date now "@yearly") => (date 2016 01 01 00 00 00 000)
             (next-date now "@annually") => (date 2016 01 01 00 00 00 000)
             (next-date now "@monthly") => (date 2015 02 01 00 00 00 000)
             (next-date now "@weekly") => (date 2015 01 05 00 00 00 000)
             (next-date now "@daily") => (date 2015 01 02 00 00 00 000)
             (next-date now "@midnight") => (date 2015 01 02 00 00 00 000)
             (next-date now "@hourly") => (date 2015 01 01 13 00 00 000)
             (next-date (t/date-time 2015 04 22 06 22 29 000) "30 22 6 * * 3") => (date 2015 04 22 06 22 30 000)
             (next-date (t/date-time 2015 04 21 06 22 30 000) "30 22 6 * * 3") => (date 2015 04 22 06 22 30 000)))

;; TODO: close to new year, combinations, range/n for dow

(facts "should return nil for an invalid cron expression"
       (next-date now "x * * * * *") => nil
       (next-date now "* x * * * *") => nil
       (next-date now "* * x * * *") => nil
       (next-date now "* * * x * *") => nil
       (next-date now "* * * * x *") => nil
       (next-date now "* * * * * x") => nil
       (next-date now "L * * * * *") => nil
       (next-date now "* L * * * *") => nil
       (next-date now "* * L * * *") => nil
       (next-date now "* * * 1L * *") => nil
       (next-date now "* * * * L *") => nil
       (next-date now "* * * * * L") => nil
       (next-date now "W * * * * *") => nil
       (next-date now "* W * * * *") => nil
       (next-date now "* * W * * *") => nil
       (next-date now "* * * * W *") => nil
       (next-date now "61 * * * * *") => nil
       (next-date now "* 61 * * * *") => nil
       (next-date now "* * 25 * * *") => nil
       (next-date now "* * * 32 * *") => nil
       (next-date now "* * * * 13 *") => nil
       (next-date now "* * * * * * *") => nil
       (next-date now "1,62 * * * * *") => nil
       (next-date now "* 1,62 * * * *") => nil
       (next-date now "* * 1,25 * * *") => nil
       (next-date now "* * * 1,32 * *") => nil
       (next-date now "* * * * 2,13 *") => nil
       (next-date now "* * * * * 1,8") => nil
       (next-date now "1-62 * * * * *") => nil
       (next-date now "* 1-62 * * * *") => nil
       (next-date now "* * 1-25 * * *") => nil
       (next-date now "* * * 1-32 * *") => nil
       (next-date now "* * * * 2-13 *") => nil
       (next-date now "* * * * * 1-8") => nil
       (next-date now "* * * * * 8L") => nil
       (next-date now "* * * * febx *") => nil
       (next-date now "* * * * * MONx") => nil
       (next-date now "s s") => nil
       (next-date now "") => nil)

(facts "should provide next date calculated within a timezone"
       (next-date now "1 0 12 * * *" nil) => (date 2015 01 01 12 00 01 000)
       (next-date now "1 0 12 * * *" "UTC") => (date 2015 01 01 12 00 01 000)
       (next-date now "1 0 12 * * *" "Asia/Seoul") => (date 2015 01 02 03 00 01 000)
       (next-date (t/date-time 2015 01 01 12 00 02) "1 0 12 * * *" "America/Sao_Paulo") => (date 2015 01 01 14 00 01 000)
       (next-date (t/date-time 2015 01 01 12 00 02) "1 * * * * *" "America/Sao_Paulo") => (t/date-time 2015 01 01 12 01 01))

(facts "should handle short month weirdness"
       (next-date (t/date-time 2020 3 30) "0 0 0 1 * *") => (date 2020 04 01 00 00 000)
       (next-date (t/date-time 2020 1 30) "0 0 0 1 * *") => (date 2020 02 01 00 00 000))

(deftest weirdness
  (testing "real observed issue"
    (is (= (t/date-time 2025 01 28 05 02 00) (next-date (t/date-time 2025 1 28 05 00 00) "0 2 5 * * *")))
    ; ;; When we are at the exact timestamp of the cron, we should schedule for
    ; ;; the NEXT instance.
    (is (= (t/date-time 2025 01 29 05 02 00) (next-date (t/date-time 2025 1 28 05 02 00) "0 2 5 * * *")))
    (is (= (t/date-time 2025 02 01 04 12 00) (next-date (t/date-time 2025 1 30 03 00 00) "0 12 4 1,2,4 * *")))
    (is (= (t/date-time 2025 02 01 04 12 00) (next-date (t/date-time 2025 1 30 03 00 00) "0 12 4 1,2,4 * *")))
    (is (= (t/date-time 2025 02 02 04 12 00) (next-date (t/date-time 2025 1 30 03 00 00) "0 12 4 2,4 * *")))

    (is (= (t/date-time 2025 03 02 04 12 00) (next-date (t/date-time 2025 2 28 03 00 00) "0 12 4 2,4 * *")))
    (is (= (t/date-time 2025 04 02 04 12 00) (next-date (t/date-time 2025 3 30 03 00 00) "0 12 4 2,4 * *")))

    (is (= (t/date-time 2025 04 02 04 12 00) (next-date (t/date-time 2025 1 30 03 00 00) "0 12 4 2,4 4 *")))))
