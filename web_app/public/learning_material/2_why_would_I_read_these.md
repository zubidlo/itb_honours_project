> **Recap**
> - In the last section, I tried to articulate my interest in functional programming and my reasons for working on this project.
> - In this section, I will try to give you a reader a reason to continue to read these pages.

### Why Would I Read These?

In my experience, writing software is fun. For a while at least. Let me explain.

You want to build a software, so you think about its design in advance. You think about abstractions you would create, about the data-structures you will hold them in and about algorithms which will manipulate the abstractions according to requirements. Then you start to write the code and you are excited. It's fun. You are transforming your ideas into a working program, it's a miracle! For a while...

Very soon, though, you start to notice that this transformation of your ideas into a code is not all you have to do. It's not that simple. You must handle exceptions, null checks and other things not related to your design ideas. You will find yourself solving problems related to underlying computer and language instead of developing your ideas into working software very soon in the development process.

But you are not going to give up so soon, you will make this work! So you fight back and your software project grows. And it grows like a monster, mutating away. Your ideas are scattered around null checks, nested try-catch blocks and other ceremony related to libraries you are using. Every time you add something somewhere, the program breaks in five other places. All is connected together, depends on each other. Complexity grows exponentially and your capability to reason about the program degrades exponentially. It's too many things to keep in mind and it's not fun anymore! It's hard and mundane!

At this point, you ready to give up. You start to see that your initial design ideas must be adjusted to fit these problems, so you go back to the books. FAIL!!!

##### Why this happened?

Well, we can talk about the reasons for hours, but let me try to give you just one reason I consider a root of the problem. I think that the failure is a direct result of having too much power too early. Let me explain. 

In imperative programming languages, the methods are too powerful with no constraints. They can do whatever they want. They can change objects in place, write to a file, put something on a screen. And they can lie. For example, consider some File. write method, it says it writes to a file. But it's not totally true. It can as well throw 4 different exceptions. There are methods which say they do something and return a result. But they can do something else in the process as well and hide this fact from you and return null at the end if they want.

>Metaphorically it's similar to a situation when you get a powerful car which allows you to drive anywhere. You can try to reach your destination by straight line path driving through whatever. Forests, fields, somebody else garden. It looks like a great idea in the beginning until you end up in a ditch or a river with no way out.

##### Whats the alternative?

Functional Programming of course :-) !!!

Functional programming is a programming with functions, not methods. Functions are much less powerful, they have various constraints, but in exchange, they get various very useful properties which make reasoning about them straight forward. Functions do what they say they do and never lie. They don't throw exceptions or return nulls. When they need to write to a file, they do it the way that the change in a file is encapsulated in the result they return to you. 

They are associative, pluggable and composable, the same way the Lego is. You build your software by composing simple functions the same way as you would build a house from Lego blocks. Your software complexity doesn't grow exponentially. Instead, it's just a function composed of other functions, which are all simple and replaceable by design. The capability to reason about the software stays constant.

Also, the solution space is smaller. There is just a number of ideas you again compose to design your software. You just have to learn a few ideas and how to compose them together. There are design patterns, but it is not 40 of them.

>In a metaphor, it's like to have a cap capable only of driving on the roads. You have much less power at the beginning, but you will reach your destination with a minimal effort and safely given that a road leads there.

This methaphore was taken from a talk [Functional Programming is Terrible](https://www.youtube.com/watch?v=hzf3hTUKk8U&list=PLWLFcD9reEAqTBKnEPBD4hrN1kLi0xhb3&index=10) given by Runar Bjarnason.



