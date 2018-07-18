# Anonymous Inner Classes

Inner classes without a name and for which only a single object is created. Anonymous inner classes are often used to declare and instantiate a class that implements an interface or extends an abstract class.

Anonymous inner classes are useful in writing implementation classes for listener interfaces.
They are commonly employed with the OnClickListener Interface in Android.

```java
myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              // perform some action
            }
});
```

(Lambda expressions can be used now that Android supports Java 8.)
```java
myView.setOnClickListener(() -> {
  //do something
});

```

Some differences from normal classes:
* A normal class can implement any number of interfaces but an anonymous inner class can implement only one interface at a time.
* A regular class can extend a class and implement any number of interface simultaneously, but anonymous inner classes can extend a class or can implement an interface but not both.
