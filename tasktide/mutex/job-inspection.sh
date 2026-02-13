NfsMutex.waitForLock() 
  -> At one point was valid to wait until has active
  -> All decisions are now placed to the Strategy
  -> Meaning when a retry needs to occur
       -> The leader reference is lost, and stays null
  -> Retry instances ~3-4/25
  -> Commenting resolved issue take 18:41:59.540 -> 18:43:37.647
  -> Test completes itself now

./gradlew clean :itemstore:test --tests "org.tasktide.itemstore.mutex.ComplexMutexOrchestratorTests.canMultipleProcessesLockActionReleaseQueue"


2026-02-10 18:37:54.993 INFO  [ main -> org.tasktide.itemstore.mutex.NfsMutex.waitForLock ]: Process-14834 waiting until no active leader
2026-02-10 18:37:54.994 DEBUG [ main -> org.tasktide.itemstore.mutex.utils.MutexFilesUtils.waitJitterTime ]: Process-14834 waiting '298'ms
2026-02-10 18:37:55.293 DEBUG [ main -> org.tasktide.itemstore.mutex.utils.MutexFilesUtils.waitJitterTime ]: Process-14834 waiting complete '298'ms
2026-02-10 18:37:55.294 INFO  [ main -> org.tasktide.itemstore.mutex.NfsMutex.waitForLock ]: Process-14834 waiting until no active leader
2026-02-10 18:37:55.295 DEBUG [ main -> org.tasktide.itemstore.mutex.utils.MutexFilesUtils.waitJitterTime ]: Process-14834 waiting '273'ms


ps -eo pid,ppid,cmd,lstart,tty,etimes | grep -E "3538|3668|3949"
   3538    3462 java -cp /home/bren/.gradle Tue Feb 10 17:30:34 2026 ?            570
   3668    3462 java -cp /home/bren/.gradle Tue Feb 10 17:30:34 2026 ?            570
   3949    3462 java -cp /home/bren/.gradle Tue Feb 10 17:30:34 2026 ?            570
   6520    3512 grep --color=auto -E 3538|3 Tue Feb 10 17:40:04 2026 pts/2          0
bren@DESKTOP-57RHCJ2:/mnt/c/Users/Brendan Kenna/GitHub/TaskTide/tasktide/itemstore$ jstack
Usage:
    jstack [-l][-e] <pid>
        (to connect to running process)

Options:
    -l  long listing. Prints additional information about locks
    -e  extended listing. Prints additional information about threads
    -? -h --help -help to print this help message
bren@DESKTOP-57RHCJ2:/mnt/c/Users/Brendan Kenna/GitHub/TaskTide/tasktide/itemstore$ jstack 3538
2026-02-10 17:42:28
Full thread dump OpenJDK 64-Bit Server VM (17.0.16+8-Ubuntu-0ubuntu124.04.1 mixed mode, sharing):

Threads class SMR info:
_java_thread_list=0x000073c74c021190, length=12, elements={
0x000073c80801b240, 0x000073c80814c630, 0x000073c80814da20, 0x000073c808155a30,
0x000073c808156df0, 0x000073c808158210, 0x000073c808159c50, 0x000073c80815b190,
0x000073c80815c610, 0x000073c808165200, 0x000073c808168250, 0x000073c778000ea0
}

"main" #1 prio=5 os_prio=0 cpu=7302.40ms elapsed=703.42s tid=0x000073c80801b240 nid=0xdd4 waiting on condition  [0x000073c80c9fd000]
   java.lang.Thread.State: TIMED_WAITING (sleeping)
        at java.lang.Thread.sleep(java.base@17.0.16/Native Method)
        at java.lang.Thread.sleep(java.base@17.0.16/Thread.java:344)
        at java.util.concurrent.TimeUnit.sleep(java.base@17.0.16/TimeUnit.java:446)
        at org.tasktide.itemstore.mutex.utils.MutexFilesUtils.waitJitterTime(MutexFilesUtils.java:267)
        at org.tasktide.itemstore.mutex.NfsMutex.waitForLock(NfsMutex.java:68)
        at java.lang.invoke.DirectMethodHandle$Holder.invokeVirtual(java.base@17.0.16/DirectMethodHandle$Holder)
        at java.lang.invoke.LambdaForm$MH/0x000073c7902d0c00.invoke(java.base@17.0.16/LambdaForm$MH)
        at java.lang.invoke.LambdaForm$MH/0x000073c7902cf800.invoke(java.base@17.0.16/LambdaForm$MH)
        at java.lang.invoke.LambdaForm$MH/0x000073c7902c0800.invokeExact_MT(java.base@17.0.16/LambdaForm$MH)
        at java.lang.invoke.MethodHandle.invokeWithArguments(java.base@17.0.16/MethodHandle.java:732)
        at org.mockito.internal.util.reflection.InstrumentationMemberAccessor$Dispatcher$ByteBuddy$Ak2hU1b5.invokeWithArguments(Unknown Source)
        at org.mockito.internal.util.reflection.InstrumentationMemberAccessor.invoke(InstrumentationMemberAccessor.java:251)
        at org.mockito.internal.util.reflection.ModuleMemberAccessor.invoke(ModuleMemberAccessor.java:55)
        at org.mockito.internal.creation.bytebuddy.MockMethodAdvice.tryInvoke(MockMethodAdvice.java:314)
        at org.mockito.internal.creation.bytebuddy.MockMethodAdvice$RealMethodCall.invoke(MockMethodAdvice.java:234)
        at org.mockito.internal.invocation.InterceptedInvocation.callRealMethod(InterceptedInvocation.java:142)
        at org.mockito.internal.stubbing.answers.CallsRealMethods.answer(CallsRealMethods.java:45)
        at org.mockito.Answers.answer(Answers.java:90)
        at org.mockito.internal.handler.MockHandlerImpl.handle(MockHandlerImpl.java:111)
        at org.mockito.internal.handler.NullResultGuardian.handle(NullResultGuardian.java:29)
        at org.mockito.internal.handler.InvocationNotifierHandler.handle(InvocationNotifierHandler.java:34)
        at org.mockito.internal.creation.bytebuddy.MockMethodInterceptor.doIntercept(MockMethodInterceptor.java:82)
        at org.mockito.internal.creation.bytebuddy.MockMethodAdvice.handle(MockMethodAdvice.java:134)
        at org.tasktide.itemstore.mutex.NfsMutex.waitForLock(NfsMutex.java:66)
        at org.tasktide.itemstore.mutex.NfsMutex.acquire(NfsMutex.java:98)
        - locked <0x000000008d3993a8> (a org.tasktide.itemstore.mutex.NfsMutex)
        at java.lang.invoke.LambdaForm$DMH/0x000073c7902cec00.invokeVirtual(java.base@17.0.16/LambdaForm$DMH)
        at java.lang.invoke.LambdaForm$MH/0x000073c7902d0000.invoke(java.base@17.0.16/LambdaForm$MH)
        at java.lang.invoke.LambdaForm$MH/0x000073c7902cf800.invoke(java.base@17.0.16/LambdaForm$MH)
        at java.lang.invoke.LambdaForm$MH/0x000073c7902c0800.invokeExact_MT(java.base@17.0.16/LambdaForm$MH)
        at java.lang.invoke.MethodHandle.invokeWithArguments(java.base@17.0.16/MethodHandle.java:732)
        at org.mockito.internal.util.reflection.InstrumentationMemberAccessor$Dispatcher$ByteBuddy$Ak2hU1b5.invokeWithArguments(Unknown Source)
        at org.mockito.internal.util.reflection.InstrumentationMemberAccessor.invoke(InstrumentationMemberAccessor.java:251)
        at org.mockito.internal.util.reflection.ModuleMemberAccessor.invoke(ModuleMemberAccessor.java:55)
        at org.mockito.internal.creation.bytebuddy.MockMethodAdvice.tryInvoke(MockMethodAdvice.java:314)
        at org.mockito.internal.creation.bytebuddy.MockMethodAdvice$RealMethodCall.invoke(MockMethodAdvice.java:234)
        at org.mockito.internal.invocation.InterceptedInvocation.callRealMethod(InterceptedInvocation.java:142)
        at org.tasktide.itemstore.mutex.LockActionReleaseApp.lambda$wireRealMethodsWithLogging$0(LockActionReleaseApp.java:61)
        at org.tasktide.itemstore.mutex.LockActionReleaseApp$$Lambda$195/0x000073c7902c42e0.answer(Unknown Source)
        at org.mockito.internal.stubbing.StubbedInvocationMatcher.answer(StubbedInvocationMatcher.java:42)
        at org.mockito.internal.handler.MockHandlerImpl.handle(MockHandlerImpl.java:103)
        at org.mockito.internal.handler.NullResultGuardian.handle(NullResultGuardian.java:29)
        at org.mockito.internal.handler.InvocationNotifierHandler.handle(InvocationNotifierHandler.java:34)
        at org.mockito.internal.creation.bytebuddy.MockMethodInterceptor.doIntercept(MockMethodInterceptor.java:82)
        at org.mockito.internal.creation.bytebuddy.MockMethodAdvice.handle(MockMethodAdvice.java:134)
        at org.tasktide.itemstore.mutex.NfsMutex.acquire(NfsMutex.java:98)
        - locked <0x000000008d3993a8> (a org.tasktide.itemstore.mutex.NfsMutex)
        at org.tasktide.itemstore.mutex.orchestrator.MutexOrchestrator.performLock(MutexOrchestrator.java:197)
        at org.tasktide.itemstore.mutex.orchestrator.MutexOrchestrator.acquireLock(MutexOrchestrator.java:135)
        at org.tasktide.itemstore.mutex.orchestrator.MutexOrchestrator.tryAcquireUntilSuccess(MutexOrchestrator.java:154)
        at org.tasktide.itemstore.mutex.LockActionReleaseApp.main(LockActionReleaseApp.java:122)

"Reference Handler" #2 daemon prio=10 os_prio=0 cpu=0.78ms elapsed=703.41s tid=0x000073c80814c630 nid=0xdeb waiting on condition  [0x000073c7e8a3d000]       
   java.lang.Thread.State: RUNNABLE
        at java.lang.ref.Reference.waitForReferencePendingList(java.base@17.0.16/Native Method)
        at java.lang.ref.Reference.processPendingReferences(java.base@17.0.16/Reference.java:253)
        at java.lang.ref.Reference$ReferenceHandler.run(java.base@17.0.16/Reference.java:215)

"Finalizer" #3 daemon prio=8 os_prio=0 cpu=0.22ms elapsed=703.41s tid=0x000073c80814da20 nid=0xded in Object.wait()  [0x000073c7e893d000]
   java.lang.Thread.State: WAITING (on object monitor)
        at java.lang.Object.wait(java.base@17.0.16/Native Method)
        - waiting on <0x0000000086f002e8> (a java.lang.ref.ReferenceQueue$Lock)
        at java.lang.ref.ReferenceQueue.remove(java.base@17.0.16/ReferenceQueue.java:155)
        - locked <0x0000000086f002e8> (a java.lang.ref.ReferenceQueue$Lock)
        at java.lang.ref.ReferenceQueue.remove(java.base@17.0.16/ReferenceQueue.java:176)
        at java.lang.ref.Finalizer$FinalizerThread.run(java.base@17.0.16/Finalizer.java:172)

"Signal Dispatcher" #4 daemon prio=9 os_prio=0 cpu=0.45ms elapsed=703.40s tid=0x000073c808155a30 nid=0xdf1 waiting on condition  [0x0000000000000000]        
   java.lang.Thread.State: RUNNABLE

"Service Thread" #5 daemon prio=9 os_prio=0 cpu=1.96ms elapsed=703.40s tid=0x000073c808156df0 nid=0xdf2 runnable  [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Monitor Deflation Thread" #6 daemon prio=9 os_prio=0 cpu=60.56ms elapsed=703.40s tid=0x000073c808158210 nid=0xdf3 runnable  [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C2 CompilerThread0" #7 daemon prio=9 os_prio=0 cpu=2759.95ms elapsed=703.40s tid=0x000073c808159c50 nid=0xdf4 waiting on condition  [0x0000000000000000]    
   java.lang.Thread.State: RUNNABLE
   No compile task

"C1 CompilerThread0" #15 daemon prio=9 os_prio=0 cpu=584.44ms elapsed=703.40s tid=0x000073c80815b190 nid=0xdf5 waiting on condition  [0x0000000000000000]    
   java.lang.Thread.State: RUNNABLE
   No compile task

"Sweeper thread" #19 daemon prio=9 os_prio=0 cpu=3.26ms elapsed=703.40s tid=0x000073c80815c610 nid=0xdf6 runnable  [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Notification Thread" #20 daemon prio=9 os_prio=0 cpu=0.13ms elapsed=703.37s tid=0x000073c808165200 nid=0xe40 runnable  [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Common-Cleaner" #21 daemon prio=8 os_prio=0 cpu=1.45ms elapsed=703.37s tid=0x000073c808168250 nid=0xe49 in Object.wait()  [0x000073c78eefd000]
   java.lang.Thread.State: TIMED_WAITING (on object monitor)
        at java.lang.Object.wait(java.base@17.0.16/Native Method)
        - waiting on <0x0000000086f00528> (a java.lang.ref.ReferenceQueue$Lock)
        at java.lang.ref.ReferenceQueue.remove(java.base@17.0.16/ReferenceQueue.java:155)
        - locked <0x0000000086f00528> (a java.lang.ref.ReferenceQueue$Lock)
        at jdk.internal.ref.CleanerImpl.run(java.base@17.0.16/CleanerImpl.java:140)
        at java.lang.Thread.run(java.base@17.0.16/Thread.java:840)
        at jdk.internal.misc.InnocuousThread.run(java.base@17.0.16/InnocuousThread.java:162)

"Attach Listener" #25 daemon prio=9 os_prio=0 cpu=3.80ms elapsed=690.27s tid=0x000073c778000ea0 nid=0x11cb waiting on condition  [0x0000000000000000]        
   java.lang.Thread.State: RUNNABLE

"GC Thread#3" os_prio=0 cpu=0.59ms elapsed=680.64s tid=0x000073c78800cfa0 nid=0x1327 runnable

"GC Thread#6" os_prio=0 cpu=0.31ms elapsed=680.64s tid=0x000073c788010560 nid=0x132a runnable

"GC Thread#5" os_prio=0 cpu=0.38ms elapsed=680.64s tid=0x000073c78800fcb0 nid=0x1329 runnable

"GC Thread#4" os_prio=0 cpu=0.31ms elapsed=680.64s tid=0x000073c788009ec0 nid=0x1328 runnable

"GC Thread#2" os_prio=0 cpu=10.57ms elapsed=694.82s tid=0x000073c78800d630 nid=0x105d runnable

"GC Thread#1" os_prio=0 cpu=5.41ms elapsed=694.83s tid=0x000073c7880072d0 nid=0x105b runnable

"VM Periodic Task Thread" os_prio=0 cpu=319.86ms elapsed=703.37s tid=0x000073c808166b50 nid=0xe42 waiting on condition

"VM Thread" os_prio=0 cpu=68.28ms elapsed=703.41s tid=0x000073c808148690 nid=0xde7 runnable

"G1 Service" os_prio=0 cpu=100.91ms elapsed=703.42s tid=0x000073c80811ae30 nid=0xddc runnable

"G1 Refine#0" os_prio=0 cpu=0.29ms elapsed=703.42s tid=0x000073c808119f20 nid=0xdda runnable

"G1 Conc#0" os_prio=0 cpu=0.18ms elapsed=703.42s tid=0x000073c808087a30 nid=0xdd9 runnable

"G1 Main Marker" os_prio=0 cpu=0.10ms elapsed=703.42s tid=0x000073c808086aa0 nid=0xdd8 runnable

"GC Thread#0" os_prio=0 cpu=15.51ms elapsed=703.42s tid=0x000073c8080761d0 nid=0xdd6 runnable

JNI global refs: 25, weak refs: 0




jstack 13026

2026-02-10 18:21:50
Full thread dump OpenJDK 64-Bit Server VM (17.0.16+8-Ubuntu-0ubuntu124.04.1 mixed mode, sharing):

Threads class SMR info:
_java_thread_list=0x0000738f94032610, length=14, elements={
0x000073903401b240, 0x000073903414c630, 0x000073903414da20, 0x0000739034155a30,
0x0000739034156df0, 0x0000739034158210, 0x0000739034159c50, 0x000073903415b190,
0x000073903415c610, 0x0000739034165150, 0x00007390341681a0, 0x0000738fa4000ea0,
0x0000738f940305f0, 0x0000738f94031d40
}

"main" #1 prio=5 os_prio=0 cpu=2908.22ms elapsed=95.05s tid=0x000073903401b240 nid=0x32f1 waiting on condition  [0x00007390393fe000]
   java.lang.Thread.State: TIMED_WAITING (sleeping)
        at java.lang.Thread.sleep(java.base@17.0.16/Native Method)
        at java.lang.Thread.sleep(java.base@17.0.16/Thread.java:344)
        at java.util.concurrent.TimeUnit.sleep(java.base@17.0.16/TimeUnit.java:446)
        at org.tasktide.itemstore.mutex.utils.MutexFilesUtils.waitJitterTime(MutexFilesUtils.java:270)
        at org.tasktide.itemstore.mutex.NfsMutex.waitForLock(NfsMutex.java:68)
        at org.tasktide.itemstore.mutex.NfsMutex.acquire(NfsMutex.java:98)
        - locked <0x000000008dc022d8> (a org.tasktide.itemstore.mutex.NfsMutex)
        at org.tasktide.itemstore.mutex.orchestrator.MutexOrchestrator.performLock(MutexOrchestrator.java:197)
        at org.tasktide.itemstore.mutex.orchestrator.MutexOrchestrator.acquireLock(MutexOrchestrator.java:135)
        at org.tasktide.itemstore.mutex.orchestrator.MutexOrchestrator.tryAcquireUntilSuccess(MutexOrchestrator.java:154)
        at org.tasktide.itemstore.mutex.LockActionReleaseApp.main(LockActionReleaseApp.java:95)

"Reference Handler" #2 daemon prio=10 os_prio=0 cpu=0.57ms elapsed=95.03s tid=0x000073903414c630 nid=0x3330 waiting on condition  [0x0000739009725000]
   java.lang.Thread.State: RUNNABLE
        at java.lang.ref.Reference.waitForReferencePendingList(java.base@17.0.16/Native Method)
        at java.lang.ref.Reference.processPendingReferences(java.base@17.0.16/Reference.java:253)
        at java.lang.ref.Reference$ReferenceHandler.run(java.base@17.0.16/Reference.java:215)

"Finalizer" #3 daemon prio=8 os_prio=0 cpu=0.34ms elapsed=95.03s tid=0x000073903414da20 nid=0x3332 in Object.wait()  [0x0000739009625000]
   java.lang.Thread.State: WAITING (on object monitor)
        at java.lang.Object.wait(java.base@17.0.16/Native Method)
        - waiting on <0x000000008cd50538> (a java.lang.ref.ReferenceQueue$Lock)
        at java.lang.ref.ReferenceQueue.remove(java.base@17.0.16/ReferenceQueue.java:155)
        - locked <0x000000008cd50538> (a java.lang.ref.ReferenceQueue$Lock)
        at java.lang.ref.ReferenceQueue.remove(java.base@17.0.16/ReferenceQueue.java:176)
        at java.lang.ref.Finalizer$FinalizerThread.run(java.base@17.0.16/Finalizer.java:172)

"Signal Dispatcher" #4 daemon prio=9 os_prio=0 cpu=0.63ms elapsed=95.02s tid=0x0000739034155a30 nid=0x3334 waiting on condition  [0x0000000000000000]        
   java.lang.Thread.State: RUNNABLE

"Service Thread" #5 daemon prio=9 os_prio=0 cpu=0.87ms elapsed=95.02s tid=0x0000739034156df0 nid=0x3335 runnable  [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Monitor Deflation Thread" #6 daemon prio=9 os_prio=0 cpu=6.78ms elapsed=95.02s tid=0x0000739034158210 nid=0x3336 runnable  [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C2 CompilerThread0" #7 daemon prio=9 os_prio=0 cpu=1358.23ms elapsed=95.02s tid=0x0000739034159c50 nid=0x3339 waiting on condition  [0x0000000000000000]    
   java.lang.Thread.State: RUNNABLE
   No compile task

"C1 CompilerThread0" #15 daemon prio=9 os_prio=0 cpu=299.13ms elapsed=95.02s tid=0x000073903415b190 nid=0x333b waiting on condition  [0x0000000000000000]    
   java.lang.Thread.State: RUNNABLE
   No compile task

"Sweeper thread" #19 daemon prio=9 os_prio=0 cpu=0.20ms elapsed=95.02s tid=0x000073903415c610 nid=0x333e runnable  [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Notification Thread" #20 daemon prio=9 os_prio=0 cpu=0.07ms elapsed=95.00s tid=0x0000739034165150 nid=0x3361 runnable  [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Common-Cleaner" #21 daemon prio=8 os_prio=0 cpu=0.66ms elapsed=95.00s tid=0x00007390341681a0 nid=0x3365 in Object.wait()  [0x0000739008d24000]
   java.lang.Thread.State: TIMED_WAITING (on object monitor)
        at java.lang.Object.wait(java.base@17.0.16/Native Method)
        - waiting on <0x000000008ce220b8> (a java.lang.ref.ReferenceQueue$Lock)
        at java.lang.ref.ReferenceQueue.remove(java.base@17.0.16/ReferenceQueue.java:155)
        - locked <0x000000008ce220b8> (a java.lang.ref.ReferenceQueue$Lock)
        at jdk.internal.ref.CleanerImpl.run(java.base@17.0.16/CleanerImpl.java:140)
        at java.lang.Thread.run(java.base@17.0.16/Thread.java:840)
        at jdk.internal.misc.InnocuousThread.run(java.base@17.0.16/InnocuousThread.java:162)

"Attach Listener" #24 daemon prio=9 os_prio=0 cpu=0.68ms elapsed=0.10s tid=0x0000738fa4000ea0 nid=0x36ce waiting on condition  [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C1 CompilerThread1" #16 daemon prio=9 os_prio=0 cpu=1.26ms elapsed=0.03s tid=0x0000738f940305f0 nid=0x36cf waiting on condition  [0x0000000000000000]       
   java.lang.Thread.State: RUNNABLE
   No compile task

"C1 CompilerThread2" #17 daemon prio=9 os_prio=0 cpu=1.30ms elapsed=0.03s tid=0x0000738f94031d40 nid=0x36d0 waiting on condition  [0x0000000000000000]       
   java.lang.Thread.State: RUNNABLE
   No compile task

"GC Thread#2" os_prio=0 cpu=0.23ms elapsed=87.04s tid=0x0000738fb400d8e0 nid=0x347b runnable

"GC Thread#1" os_prio=0 cpu=5.17ms elapsed=87.05s tid=0x0000738fb40072d0 nid=0x3477 runnable

"VM Periodic Task Thread" os_prio=0 cpu=34.89ms elapsed=95.00s tid=0x0000739034166aa0 nid=0x3362 waiting on condition

"VM Thread" os_prio=0 cpu=8.10ms elapsed=95.03s tid=0x0000739034148690 nid=0x332c runnable

"G1 Service" os_prio=0 cpu=9.86ms elapsed=95.05s tid=0x000073903411ae30 nid=0x3303 runnable

"G1 Refine#0" os_prio=0 cpu=0.13ms elapsed=95.05s tid=0x0000739034119f20 nid=0x3302 runnable

"G1 Conc#0" os_prio=0 cpu=0.12ms elapsed=95.05s tid=0x0000739034087a30 nid=0x32ff runnable

"G1 Main Marker" os_prio=0 cpu=0.26ms elapsed=95.05s tid=0x0000739034086aa0 nid=0x32fe runnable

"GC Thread#0" os_prio=0 cpu=7.71ms elapsed=95.05s tid=0x00007390340761d0 nid=0x32fd runnable

JNI global refs: 23, weak refs: 0

bren@DESKTOP-57RHCJ2:/mnt/c/Users/Brendan Kenna/GitHub/TaskTide/tasktide/itemstore$gre