;;; For Exercise 2

(def ^:dynamic current-message)
(def ^:dynamic current-arguments)
(def ^:dynamic holder-of-current-method)

(def apply-message-to
     (fn [method-holder instance message args]
       (let [target-holder (find-containing-holder-symbol (:__own_symbol__ method-holder)
                                                          message)]
         (if target-holder
           (binding [this instance
                    current-message message
                    current-arguments args
                    holder-of-current-method target-holder
                    ]
             (apply (message (held-methods target-holder)) args))
           (send-to instance :method-missing message args)))))

;;prove

(send-to Klass :new
         'DynamicPoint 'Point
         {
          :shift
          (fn [xinc yinc]
            (println "Method" current-message "found in" holder-of-current-method)
            (println "It has these arguments:" current-arguments))
         }
         {})

(def point (send-to DynamicPoint :new 1 2))
(send-to point :shift 100 200)


;;; For exercise 3


(def throw-no-superclass-method-error
     (fn []
       (throw (Error. (str "No superclass method `" current-message
                           "` above `" holder-of-current-method
                           "`.")))))

(def next-higher-holder-or-die
  (fn []
    (let [next-higher-holder (method-holder-symbol-above holder-of-current-method)
          next-higher-holder-message (current-message (held-methods next-higher-holder))]
      (if (nil? next-higher-holder-message)
        (throw-no-superclass-method-error)
        next-higher-holder
      )
    )

))

;;book solution
(def next-higher-holder-or-die
     (fn []
       (let [first-candidate (method-holder-symbol-above holder-of-current-method)]
         (or (find-containing-holder-symbol first-candidate current-message)
             (throw-no-superclass-method-error)))))



;;prove
(binding [current-message :to-string 
          holder-of-current-method 'Point]
        (next-higher-holder-or-die))
;;Anything
(binding [current-message :shift
          holder-of-current-method 'Point]
            (next-higher-holder-or-die))
;;Error No superclass method `:shift` above `Point`. [...]

;;; For Exercise 4

(def send-super
     (fn [& args]
       (binding [holder-of-current-method (next-higher-holder-or-die)
                 current-arguments args]
         (apply (current-message (held-methods holder-of-current-method)) args))))



(send-to Klass :new
         'ExaggeratingPoint 'Point
         {
          :shift
          (fn [xinc yinc]
            (send-super (* 100 xinc) (* 100 yinc)))
         }
         {})
          
(def braggart (send-to ExaggeratingPoint :new 1 2))
(prn (send-to braggart :shift 1 2))  ;; A point at 101, 202



(send-to Klass :new
                'SuperDuperExaggeratingPoint 'ExaggeratingPoint
                {
                 :shift
                 (fn [xinc yinc]
                   (send-super (* 1234 xinc) (* 1234 yinc)))
                 }
                {})

(def super-braggart (send-to SuperDuperExaggeratingPoint :new 1 2))
(send-to super-braggart :shift 1 2)  ; a point at 123401, 246802


;;; For exercise 5

(def repeat-to-super
     (fn []
       (binding [holder-of-current-method (next-higher-holder-or-die)]
         (apply (current-message (held-methods holder-of-current-method)) current-arguments))))



(send-to Klass :new
         'Upper 'Anything
         {
          :super-exists
          (fn [& args]
            (str "Got these args: " args))
          }
         {})


(send-to Klass :new
         'Lower 'Upper
         {
          :super-exists (fn [& args] (repeat-to-super))
          ;; If you like, you can use this to check whether
          ;; an attempt to repeat to a nonexistent super-method
          ;; correctly errors out.
          :super-missing (fn [& args] (repeat-to-super))
         }
         {})

(send-to Klass :new
         'Lowest 'Upper
         {}
         {})


(def object (send-to Lowest :new))
(println (send-to object :super-exists 1 2 3))

