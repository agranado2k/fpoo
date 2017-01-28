(def point {:x 1, :y 2, :__class_symbol__ 'Point})

(def Point
     (fn [x y]
       {:x x,
        :y y
        :__class_symbol__ 'Point}))

(def x :x)
(def y :y)
(def class-of :__class_symbol__)

(def shift
     (fn [this xinc yinc]
       (Point (+ (x this) xinc)
              (+ (y this) yinc))))

(def Triangle
     (fn [point1 point2 point3]
       {:point1 point1, :point2 point2, :point3 point3
        :__class_symbol__ 'Triangle}))


(def right-triangle (Triangle (Point 0 0)
                              (Point 0 1)
                              (Point 1 0)))

(def equal-right-triangle (Triangle (Point 0 0)
                                    (Point 0 1)
                                    (Point 1 0)))

(def different-triangle (Triangle (Point 0 0)
                                  (Point 0 10)
                                  (Point 10 0)))

(def add 
      (fn [point1 point2]
        (Point (+ (x point1) (x point2))
               (+ (y point1) (y point2)))))

(def add
      (fn [point1 point2]
        (shift point1 (x point2) (y point2))))

(def make
      (fn [& args]
        (apply (first args) (rest args))))

(def equal-points?
      (fn [point1 point2]
          (and 
            (= (x point1) (x point2))
            (= (y point1) (y point2)))))

(def equal-triangles? 
      (fn [triangle1 triangle2]
        (and
          (equal-points? (:point1 triangle1) (:point1 triangle2))
          (equal-points? (:point2 triangle1) (:point2 triangle2))
          (equal-points? (:point3 triangle1) (:point3 triangle2)))))

(def equal-points?
      (fn [& points]
          (and 
            (apply = (map x points))
            (apply = (map y points)))))

(def equal-triangles? 
      (fn [& triangles]
        (and
          (apply equal-points? (map :point1 triangles))
          (apply equal-points? (map :point2 triangles))
          (apply equal-points? (map :point3 triangles)))))

(def valid-triangle?
      (fn [point1 point2 point3]
        (and
          (not= point1 point2)
          (not= point1 point3)
          (not= point2 point3))))



        
