A transpiler that processes Java classfiles and emits JavaScript code from the classfiles.
It was abandoned a few years ago because we transitioned to TeaVM.

Some prominent features:

* **Execution of parts of code while building**. All class initializers will be run in an isolated environment 
  while transpiling, then the initial state of the application will be serialized into JS objects and can
  be accessed at runtime as normal objects. Code optimizations such as dead-code elimination also
  access the saved initial state. 
  This feature is mostly useful because the app can use standard JDK reflection at startup. Even MethodHandles can be 
  saved at build-time and invoked at runtime.
* **Scalar replacement**. This means that when the app allocates object that is not stored or passed to somebody else, 
  then the allocations will be removed, and the fields are treated as local variables.
  If combined with inlining, this enables transforming for example a 3D geometry
  calculation with many vector and matrix objects into an allocation-free code.

Main issues:

* Too long generated code. This is caused by excessive inlining, but is difficult to fix, because
  less inlining makes other optimizations to not work well.
* Exception support is missing

This project is mainly oriented towards JS, but is easily extensible to other target languages.
