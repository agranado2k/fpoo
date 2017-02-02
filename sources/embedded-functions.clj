(def make
     (fn [type & args]
       (apply type args)))

(def send-to
     (fn [object message & args]
       (apply (message (:__methods__ object)) object args)))

(def Point
     (fn [x y]
       {:x x,
        :y y
        :__class_symbol__ 'Point
        :__methods__ {
            :x :x,
            :y :y,
            :class :__class_symbol__
            :shift (fn [this xinc yinc]
                    (make Point (+ (send-to this :x) xinc)
                                (+ (send-to this :y) yinc)))
            :add (fn [this, point]
                    (send-to this :shift (send-to point :x) 
                                          (send-to point :y))
              )}}))


;; Test Cases
(def my-point (make Point 1 2))
(prn (send-to my-point :x))
(prn (send-to my-point :y))
(prn (send-to my-point :shift -1 -100))
(prn (send-to my-point :add (make Point -1 -100)))



;;;;;;
(def Point
  {
    :__instance_methods__ {
      :add-instance-values (fn [this x y]
        (assoc this :x x :y y))

      :shift (fn [this xinc yinc]
        (make Point (+ (:x this) xinc)
                    (+ (:y this) yinc)))
    }
    })
