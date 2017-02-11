(load-file "sources/klass-1.clj")

;; This imports a function from another namespace. (Think package or module.)
(use '[clojure.pprint :only [cl-format]])
;;; The two class/pairs from which everything else can be built

;; Anything
(install (basic-class 'Anything,
                      :left 'MetaAnything,
                      :up nil,
                      {
                       :add-instance-values identity
                       :method-missing
                       (fn [this message args]
                         (throw (Error. (cl-format nil "A ~A does not accept the message ~A."
                                                   (send-to this :class-name)
                                                   message))))
                       :to-string (fn [this] (str this))
                       :class-name :__class_symbol__    
                       :class (fn [this] (class-from-instance this))
                       }))
                            
(install (basic-class 'MetaAnything,
                      :left 'Klass,
                      :up 'Klass,
                      { 
                       }))


;; Klass
(install (basic-class 'Klass,
                      :left 'MetaKlass,
                      :up 'Anything,
                      {
                       :new
                       (fn [class & args]
                         (let [seeded {:__class_symbol__ (:__own_symbol__ class)}]
                           (apply-message-to class seeded :add-instance-values args)))
                      }))
                            
(install (basic-class 'MetaKlass,
                      :left 'Klass,
                      :up 'Klass,
                      {
                       :new
                       (fn [this
                            new-class-symbol superclass-symbol
                            instance-methods class-methods]
                         ;; Metaclass
                         (install
                          (basic-class (metasymbol new-class-symbol)
                                       :left 'Klass
                                       :up 'Klass
                                       class-methods))
                         ;; Class
                         (install
                          (basic-class new-class-symbol
                                       :left (metasymbol new-class-symbol)
                                       :up superclass-symbol
                                       instance-methods)))
                       }))

;; An example class:

(send-to Klass :new
         'Point 'Anything
         {
          :x :x
          :y :y 

          :add-instance-values
          (fn [this x y]
            (assoc this :x x :y y))
          
          :to-string
          (fn [this]
            (cl-format nil "A ~A like this: [~A, ~A]"
                       (send-to this :class-name)
                       (send-to this :x)
                       (send-to this :y)))

          :shift
          (fn [this xinc yinc]
            (let [my-class (send-to this :class)]
              (send-to my-class :new
                                (+ (:x this) xinc)
                                (+ (:y this) yinc))))
          :add
          (fn [this other]
            (send-to this :shift (:x other)
                                 (:y other)))
         } 
         
         {
          :origin (fn [class] (send-to class :new 0 0))
         })

;;Exercise 1

(install (basic-class 'Klass,
                      :left 'MetaKlass,
                      :up 'Anything,
                      {
                       :new
                       (fn [class & args]
                         (let [seeded {:__class_symbol__ (:__own_symbol__ class)}]
                           (apply-message-to class seeded :add-instance-values args)))
                       :to-string (fn [this] (str "class " (:__own_symbol__ this)))
                      }))

;;Prove
(send-to Point :to-string)
;;"class Point"
(send-to Klass :to-string)
;;"class Klass"
;; As before:
(send-to (send-to Anything :new) :to-string)
;;"{:__class_symbol__ Anything}"
(send-to (send-to Point :new 1 2) :to-string)
;;"{:y 2, :x 1, :__class_symbol__ Point}"

;; Exercise 2

;; I'll mark classes invisible by tagging them with metadata.

(def invisible
     (fn [class]
       (assoc class :__invisible__ true)))

(def invisible?
     (fn [class-symbol] (:__invisible__ (eval class-symbol))))

;; Change the already-defined metaclasses to be invisible:

(def MetaAnything (invisible MetaAnything))
(def MetaKlass (invisible MetaKlass))
(def MetaPoint (invisible MetaPoint))

;; Ancestors just removes invisible classes from the
;; reversed lineage.

(def Klass
     (assoc-in Klass
               [:__instance_methods__ :ancestors]
               (fn [class]
                 (remove invisible?
                         (reverse (lineage (:__own_symbol__ class)))))))

;; New metaclasses need to be created to be invisible.

(def MetaKlass
     (assoc-in MetaKlass
               [:__instance_methods__ :new]
                (fn [this
                     new-class-symbol superclass-symbol
                     instance-methods class-methods]
                  ;; Metaclass
                  (install
                   ;; VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV     new
                   (invisible
                    ;; ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 
                    (basic-class (metasymbol new-class-symbol)
                                 :left 'Klass
                                 :up 'MetaAnything
                                 class-methods)))
                  ;; Class
                  (install
                   (basic-class new-class-symbol
                                :left (metasymbol new-class-symbol)
                                :up superclass-symbol
                                instance-methods)))))

;;Prove
(send-to Point :ancestors)
;;(Point Anything)
(send-to ColoredPoint :ancestors)
;;(ColoredPoint Point Anything)
(send-to Klass :ancestors)
;;(Klass Anything)
(send-to MetaPoint :ancestors)
;;(Klass Anything)

;; Exercise 3


;; Prove

(send-to Point :class-name)