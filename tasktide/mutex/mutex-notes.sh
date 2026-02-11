grep "APP-Conf" logs/test/itemstore/TestLog-ItemStore.log

''' --> All 25 come in at pretty much the same time

2026-02-09 16:34:28.356 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:28.375 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:28.369 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:28.380 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:28.386 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:28.402 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:28.436 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:28.446 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:28.532 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:28.570 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:28.622 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:28.710 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:28.803 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:28.819 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:28.824 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:28.866 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:28.873 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:29.555 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:29.570 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:29.595 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:29.619 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:29.707 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:30.284 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 16:34:30.630 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing

'''


grep -c "Initializing mutex" logs/test/itemstore/TestLog-ItemStore.log

''' --> Staggering does chop up the per second amount from 16:34:28/29 bins to 35,36,37,38,39,40,41

2026-02-09 16:34:35.731 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'c1348e92-c6a9-4148-b4ce-a90fe9a06bdb'
2026-02-09 16:34:36.439 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '27691e54-df17-4bcf-a368-17be7ae41572'
2026-02-09 16:34:36.464 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'a8229e1a-c360-4ec0-b6e8-f9cf1057975d'
2026-02-09 16:34:36.522 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'bc42b13e-d3eb-4806-9eb2-04bd7534a7eb'
2026-02-09 16:34:36.592 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'e9e2d62a-77ba-46ce-80da-79f8bef87c96'
2026-02-09 16:34:36.685 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '19811cb6-a1ab-4576-9a59-956b7d736c8b'
2026-02-09 16:34:36.813 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '5c0fe926-8fce-4d2e-a722-981d8cb55de4'
2026-02-09 16:34:36.824 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'dc89c0a8-1cc4-45c8-bfd0-ae9dde6e6757'
2026-02-09 16:34:37.081 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'fe82c521-e099-49ec-80e3-c7e87a36307a'
2026-02-09 16:34:37.099 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'ba157962-b48f-47ae-861b-38748c674a9a'
2026-02-09 16:34:37.125 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'cc94e461-c8e7-44e3-b5e9-17f598ffab81'
2026-02-09 16:34:37.151 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'fe5ed9b6-1e77-42cc-a94c-47b4e970f620'
2026-02-09 16:34:37.335 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '23b393e8-b7c3-4ce3-ab0d-9b49fec776b5'
2026-02-09 16:34:37.389 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '41b3cc9b-8703-4a1c-9d6e-c1998361383f'
2026-02-09 16:34:37.436 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'e92009d7-6007-4924-ba67-77428b6002a5'
2026-02-09 16:34:37.857 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'fd11a942-aa15-4d1a-af5b-b3460ba447b1'
2026-02-09 16:34:38.176 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '7e80eec4-eec6-46ab-91fd-a7be9064b396'
2026-02-09 16:34:38.347 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '2bca55a5-c996-471b-8ba4-6199fb0d976b'
2026-02-09 16:34:38.771 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '517689f5-b334-41bb-b889-e742abfa7031'
2026-02-09 16:34:39.463 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'a6d77005-4ccb-4c45-b04f-82e8d3bde884'
2026-02-09 16:34:39.486 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'e5a01f7f-2da7-4a88-80f8-a0e71e4e05bc'
2026-02-09 16:34:39.663 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'c5f6acd5-afa2-4498-8859-0bdbd68a2d9a'
2026-02-09 16:34:40.188 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '5fc006ea-f331-4083-8c41-12c164aa9cf6'
2026-02-09 16:34:41.134 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '3f7fbfa0-6fa5-4369-9d7c-85a78aab2340'
2026-02-09 16:34:41.182 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '39bd07e7-6408-4400-b58f-8893c1e39e07'

'''


grep "Leadership acquired" logs/test/itemstore/TestLog-ItemStore.log | cat -n

''' --> N = 22 3 dropped off at this point, queue structure is visible as bc42 can be seen moving positions

      3 'bc42b13e-d3eb-4806-9eb2-04bd7534a7eb'
      2 '7e80eec4-eec6-46ab-91fd-a7be9064b396'
      2 '3f7fbfa0-6fa5-4369-9d7c-85a78aab2340'
      1 'fe82c521-e099-49ec-80e3-c7e87a36307a'

3 Drop offs and 3 instances of JsonB Exception invalid EOF through evaluating leader TTL (let this fail)
27691e54-df17-4bcf-a368-17be7ae41572 -> Only recasting ballot logs
a8229e1a-c360-4ec0-b6e8-f9cf1057975d -> Only recasting ballot logs
dc89c0a8-1cc4-45c8-bfd0-ae9dde6e6757 -> Only recasting ballot logs
2026-02-09 16:35:24.769 ERROR [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: Unable to acquire lock exiting:

2026-02-09 16:35:04.630 ERROR [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: Unable to acquire lock exiting:

2026-02-09 16:35:04.631 ERROR [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: Unable to acquire lock exiting:


2026-02-09 16:34:38.966 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'c1348e92-c6a9-4148-b4ce-a90fe9a06bdb'
2026-02-09 16:34:39.600 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '19811cb6-a1ab-4576-9a59-956b7d736c8b'
2026-02-09 16:34:41.057 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'fe5ed9b6-1e77-42cc-a94c-47b4e970f620'
2026-02-09 16:34:43.702 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'ba157962-b48f-47ae-861b-38748c674a9a'
2026-02-09 16:34:46.505 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '517689f5-b334-41bb-b889-e742abfa7031'
2026-02-09 16:34:48.445 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'fd11a942-aa15-4d1a-af5b-b3460ba447b1'
2026-02-09 16:34:50.959 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'e92009d7-6007-4924-ba67-77428b6002a5'
2026-02-09 16:34:53.808 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '5fc006ea-f331-4083-8c41-12c164aa9cf6'
2026-02-09 16:34:56.612 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '2bca55a5-c996-471b-8ba4-6199fb0d976b'
2026-02-09 16:34:59.721 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'e5a01f7f-2da7-4a88-80f8-a0e71e4e05bc'
2026-02-09 16:35:02.819 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'c5f6acd5-afa2-4498-8859-0bdbd68a2d9a'
2026-02-09 16:35:03.027 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '7e80eec4-eec6-46ab-91fd-a7be9064b396'
2026-02-09 16:35:04.373 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '7e80eec4-eec6-46ab-91fd-a7be9064b396'
2026-02-09 16:35:04.601 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'bc42b13e-d3eb-4806-9eb2-04bd7534a7eb'
2026-02-09 16:35:14.915 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '41b3cc9b-8703-4a1c-9d6e-c1998361383f'
2026-02-09 16:35:15.216 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '3f7fbfa0-6fa5-4369-9d7c-85a78aab2340'
2026-02-09 16:35:15.751 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '3f7fbfa0-6fa5-4369-9d7c-85a78aab2340'
2026-02-09 16:35:16.088 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'cc94e461-c8e7-44e3-b5e9-17f598ffab81'
2026-02-09 16:35:17.266 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '39bd07e7-6408-4400-b58f-8893c1e39e07'
2026-02-09 16:35:18.305 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'e9e2d62a-77ba-46ce-80da-79f8bef87c96'
2026-02-09 16:35:19.540 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '23b393e8-b7c3-4ce3-ab0d-9b49fec776b5'
2026-02-09 16:35:20.794 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '5c0fe926-8fce-4d2e-a722-981d8cb55de4'
2026-02-09 16:35:22.712 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'bc42b13e-d3eb-4806-9eb2-04bd7534a7eb'
2026-02-09 16:35:24.439 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'a6d77005-4ccb-4c45-b04f-82e8d3bde884'
2026-02-09 16:35:30.358 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'fe82c521-e099-49ec-80e3-c7e87a36307a'
2026-02-09 16:35:30.872 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'bc42b13e-d3eb-4806-9eb2-04bd7534a7eb'

'''



grep "Leadership verified" logs/test/itemstore/TestLog-ItemStore.log
grep "File Channel Lock release" logs/test/itemstore/TestLog-ItemStore.log | grep "id" | wc -l

''' --> 22 verified, 21 confirmed did release their file channel lock

Drop offs
5c0fe926-8fce-4d2e-a722-981d8cb55de4 
      -> Unable to confirm leadership
      -> MutexOrchestrator.confirmLeader fails with ActiveMutexChecked exception
            => Executing method only catches MutexUncheckedException.
            => Resolving lead to 24/25 other failure with 
                 java.lang.NullPointerException: Cannot invoke "java.nio.file.Path.getFileSystem()" because "path" is null

2026-02-09 16:35:21.622 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Confirming leadership:    '5c0fe926-8fce-4d2e-a722-981d8cb55de4'
2026-02-09 16:35:22.129 WARN  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.tryAcquireUntilSuccess ]: Lock acquisition failed, retrying: Sanity checked leader does not match current:   5c0fe926-8fce-4d2e-a722-981d8cb55de4


2026-02-09 16:34:39.292 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'c1348e92-c6a9-4148-b4ce-a90fe9a06bdb'
2026-02-09 16:34:40.155 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '19811cb6-a1ab-4576-9a59-956b7d736c8b'
2026-02-09 16:34:42.139 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'fe5ed9b6-1e77-42cc-a94c-47b4e970f620'
2026-02-09 16:34:44.759 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'ba157962-b48f-47ae-861b-38748c674a9a'
2026-02-09 16:34:47.688 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '517689f5-b334-41bb-b889-e742abfa7031'
2026-02-09 16:34:49.556 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'fd11a942-aa15-4d1a-af5b-b3460ba447b1'
2026-02-09 16:34:52.165 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'e92009d7-6007-4924-ba67-77428b6002a5'
2026-02-09 16:34:54.874 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '5fc006ea-f331-4083-8c41-12c164aa9cf6'
2026-02-09 16:34:57.830 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '2bca55a5-c996-471b-8ba4-6199fb0d976b'
2026-02-09 16:35:00.906 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'e5a01f7f-2da7-4a88-80f8-a0e71e4e05bc'
2026-02-09 16:35:03.454 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'c5f6acd5-afa2-4498-8859-0bdbd68a2d9a'
2026-02-09 16:35:05.010 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '7e80eec4-eec6-46ab-91fd-a7be9064b396'
2026-02-09 16:35:15.477 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '41b3cc9b-8703-4a1c-9d6e-c1998361383f'
2026-02-09 16:35:16.003 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '3f7fbfa0-6fa5-4369-9d7c-85a78aab2340'
2026-02-09 16:35:16.549 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'cc94e461-c8e7-44e3-b5e9-17f598ffab81'
2026-02-09 16:35:17.871 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '39bd07e7-6408-4400-b58f-8893c1e39e07'
2026-02-09 16:35:18.812 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'e9e2d62a-77ba-46ce-80da-79f8bef87c96'
2026-02-09 16:35:20.629 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '23b393e8-b7c3-4ce3-ab0d-9b49fec776b5'
2026-02-09 16:35:24.856 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'a6d77005-4ccb-4c45-b04f-82e8d3bde884'
2026-02-09 16:35:30.800 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'fe82c521-e099-49ec-80e3-c7e87a36307a'
2026-02-09 16:35:31.185 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'bc42b13e-d3eb-4806-9eb2-04bd7534a7eb'

'''



# 3 distinct events for JsonB exception
grep -B 10 "JsonbException" ItemStore-Mutex/multi-process-lock-release-queue.log 

'''

jakarta.json.bind.JsonbException: Internal error: Invalid token=EOF at (line no=1, column no=0, offset=-1). Expected tokens are: [CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]
	at org.eclipse.yasson.internal.DeserializationContextImpl.deserializeItem(DeserializationContextImpl.java:142)
	at org.eclipse.yasson.internal.DeserializationContextImpl.deserialize(DeserializationContextImpl.java:127)
	at org.eclipse.yasson.internal.JsonBinding.deserialize(JsonBinding.java:55)
	at org.eclipse.yasson.internal.JsonBinding.fromJson(JsonBinding.java:62)
	at org.tasktide.itemstore.mutex.utils.MutexFilesUtils.readMutexFromFile(MutexFilesUtils.java:227)
	at org.tasktide.itemstore.mutex.utils.MutexFilesUtils.evaluateLeaderTimeToLive(MutexFilesUtils.java:407)
	at org.tasktide.itemstore.mutex.strategy.ElectionStrategy.evaluateLeaderTtl(ElectionStrategy.java:187)
	at org.tasktide.itemstore.mutex.strategy.ElectionStrategy.evaluateIteration(ElectionStrategy.java:271)
	at org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader(ElectionStrategy.java:317)
	at org.tasktide.itemstore.mutex.strategy.ElectionStrategy.apply(ElectionStrategy.java:80)
	at org.tasktide.itemstore.mutex.NfsMutex.waitForLock(NfsMutex.java:74)
	at org.tasktide.itemstore.mutex.NfsMutex.waitForLock(NfsMutex.java:67)
	at org.tasktide.itemstore.mutex.LockActionReleaseApp.lambda$wireRealMethodsWithLogging$0(LockActionReleaseApp.java:59)
	at org.tasktide.itemstore.mutex.NfsMutex.acquire(NfsMutex.java:99)
	at org.tasktide.itemstore.mutex.MutexOrchestrator.performLock(MutexOrchestrator.java:163)
	at org.tasktide.itemstore.mutex.MutexOrchestrator.acquireLock(MutexOrchestrator.java:132)
	at org.tasktide.itemstore.mutex.MutexOrchestrator.tryAcquireUntilSuccess(MutexOrchestrator.java:142)
	at org.tasktide.itemstore.mutex.LockActionReleaseApp.main(LockActionReleaseApp.java:120)


2026-02-09 16:35:04.630 ERROR [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: Unable to acquire lock exiting:

2026-02-09 16:35:04.631 ERROR [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: Unable to acquire lock exiting:

jakarta.json.bind.JsonbException: Internal error: Invalid token=EOF at (line no=1, column no=0, offset=-1). Expected tokens are: [CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]
jakarta.json.bind.JsonbException: Internal error: Invalid token=EOF at (line no=1, column no=0, offset=-1). Expected tokens are: [CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]
--
2026-02-09 16:35:24.769 ERROR [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: Unable to acquire lock exiting:

jakarta.json.bind.JsonbException: Internal error: Invalid token=EOF at (line no=1, column no=0, offset=-1). Expected tokens are: [CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]
        at org.eclipse.yasson.internal.DeserializationContextImpl.deserializeItem(DeserializationContextImpl.java:142)


2026-02-09 16:35:04.601 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'bc42b13e-d3eb-4806-9eb2-04bd7534a7eb'
2026-02-09 16:35:04.602 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Verifying leadership: 'bc42b13e-d3eb-4806-9eb2-04bd7534a7eb'
2026-02-09 16:35:04.604 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.apply ]: Mutex written for:      '7e80eec4-eec6-46ab-91fd-a7be9064b396'
2026-02-09 16:35:04.618 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.lambda$wireRealMethodsWithLogging$0 ]: NFS Lock acquire -> null
2026-02-09 16:35:04.619 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: NFS Lock acquired
2026-02-09 16:35:04.619 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Confirming leadership:    '7e80eec4-eec6-46ab-91fd-a7be9064b396'
2026-02-09 16:35:04.630 ERROR [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: Unable to acquire lock exiting:

2026-02-09 16:35:04.631 ERROR [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: Unable to acquire lock exiting:

jakarta.json.bind.JsonbException: Internal error: Invalid token=EOF at (line no=1, column no=0, offset=-1). Expected tokens are: [CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]
jakarta.json.bind.JsonbException: Internal error: Invalid token=EOF at (line no=1, column no=0, offset=-1). Expected tokens are: [CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]
--
2026-02-09 16:35:24.687 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.apply ]: Active leader with Id:  'a6d77005-4ccb-4c45-b04f-82e8d3bde884'
2026-02-09 16:35:24.687 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.apply ]: State set, writing mutex for:   'a6d77005-4ccb-4c45-b04f-82e8d3bde884'
2026-02-09 16:35:24.746 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.apply ]: Mutex written for:      'a6d77005-4ccb-4c45-b04f-82e8d3bde884'
2026-02-09 16:35:24.754 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.lambda$wireRealMethodsWithLogging$0 ]: NFS Lock acquire -> null
2026-02-09 16:35:24.754 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: NFS Lock acquired
2026-02-09 16:35:24.754 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Confirming leadership:    'a6d77005-4ccb-4c45-b04f-82e8d3bde884'
2026-02-09 16:35:24.762 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.evaluateLeaderTtl ]: Leader passed TTL check-in: 'C:\Users\Brendan Kenna\GitHub\TaskTide\tasktide\itemstore\ItemStore-Mutex\Queue\1770654914369.10-171-238-13_8560c2d0-c960-4dec-953d-abf742c9a92e_87857bc6-6e2f-41fe-86b4-53e7d2a46145.lock'
2026-02-09 16:35:24.762 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Resetting predecessor:        'bc42b13e-d3eb-4806-9eb2-04bd7534a7eb'
2026-02-09 16:35:24.769 ERROR [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: Unable to acquire lock exiting:

jakarta.json.bind.JsonbException: Internal error: Invalid token=EOF at (line no=1, column no=0, offset=-1). Expected tokens are: [CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]

'''