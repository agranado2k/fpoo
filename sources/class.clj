(def make
     (fn [class & args]
       (let [seeded {:__class_symbol__ (:__own_symbol__ class)}
             constructor  (:add-instance-values (:__instance_methods__ class))]
         (apply constructor seeded args))))

(def send-to
     (fn [instance message & args]
       (let [class (eval (:__class_symbol__ instance))
             method (message (:__instance_methods__ class))]
         (apply method instance args))))


(def Point
{
  :__own_symbol__ 'Point
  :__instance_methods__
  {
    :add-instance-values (fn [this x y]
                           (assoc this :x x :y y))
    :class :__class_symbol__
    :shift (fn [this xinc yinc]
             (make Point (+ (:x this) xinc)
                         (+ (:y this) yinc)))
    :add (fn [this other]
           (send-to this :shift (:x other)
                                (:y other)))
   }
 })

;; Exercise 1

(def apply-message-to
    (fn [class instance message args]
      (apply (message (:__instance_methods__ class)) instance args)
))

(def make
     (fn [class & args]
       (let [seeded {:__class_symbol__ (:__own_symbol__ class)}]
         (apply-message-to class seeded :add-instance-values  args))))

(def send-to
     (fn [instance message & args]
       (let [class (eval (:__class_symbol__ instance))]
         (apply-message-to class instance message args))))

;; Exercise 2

(def class-from-instance
      (fn [this]
        (eval (:__class_symbol__ this))))

(def Point
{
  :__own_symbol__ 'Point
  :__instance_methods__
  {
    :add-instance-values (fn [this x y]
                           (assoc this :x x :y y))
    :class-name :__class_symbol__
    :class class-from-instance
    :shift (fn [this xinc yinc]
             (make Point (+ (:x this) xinc)
                         (+ (:y this) yinc)))
    :add (fn [this other]
           (send-to this :shift (:x other)
                                (:y other)))
   }
 })


(def point (make Point 1 2))
(send-to point :class-name)
;; Point

(send-to point :class)
;;{:__own_symbol__ Point, ....}}

;; Exercise 3
(def point (make Point 1 2))

(def Point
{
  :__own_symbol__ 'Point
  :__instance_methods__
  {
    :add-instance-values (fn [this x y]
                           (assoc this :x x :y y))
    :class-name :__class_symbol__
    :class class-from-instance
    :origin (fn [this] (make Point 0 0))
    :shift (fn [this xinc yinc]
             (make Point (+ (:x this) xinc)
                         (+ (:y this) yinc)))
    :add (fn [this other]
           (send-to this :shift (:x other)
                                (:y other)))
   }
 })

(send-to point :origin)

;; For exercise 4
(def Holder  
{
  :__own_symbol__ 'Holder
  :__instance_methods__
  {
    :add-instance-values (fn [this held]
                           (assoc this :held held))
  }
})

(def choose-instance-methods-or-access-methods 
      (fn [class message]
        (if (not= (message (:__instance_methods__ class)) nil) 
          (message (:__instance_methods__ class)) 
          message)))
  
(def apply-message-to
    (fn [class instance message args]
      (apply  (choose-instance-methods-or-access-methods class message) instance args)
)) 

(def send-to
     (fn [instance message & args]
       (let [class (eval (:__class_symbol__ instance))]
         (apply-message-to class instance message args))))
