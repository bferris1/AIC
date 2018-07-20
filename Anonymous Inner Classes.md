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
mEmailSignInButton.setOnClickListener(view -> attemptLogin());
```

Some differences from normal classes:
* A normal class can implement any number of interfaces but an anonymous inner class can implement only one interface at a time.
* A regular class can extend a class and implement any number of interface simultaneously, but anonymous inner classes can extend a class or can implement an interface but not both.

Other things to note:
- An anonymous class has access to the members of its enclosing class.
    - This makes them very useful compared to defining a seperate class
- An anonymous class cannot access local variables in its enclosing scope that are not declared as final or effectively final.
