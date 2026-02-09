grep -c "APP-Conf" logs/test/itemstore/TestLog-ItemStore.log

''' --> All 25 come in at pretty much the same time

2026-02-09 17:45:08.868 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:09.026 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:09.145 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:09.160 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:09.219 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:09.314 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:09.379 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:09.430 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:09.588 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:09.607 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:09.717 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:10.075 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:10.303 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:10.336 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:10.349 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:10.376 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:10.593 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:10.722 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:11.235 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:11.238 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:11.258 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:11.504 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:11.625 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:11.678 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing
2026-02-09 17:45:11.720 INFO  [ main -> org.tasktide.itemstore.mutex.LockActionReleaseApp.main ]: APP-Configuring arguments for testing

'''


grep "Initializing mutex" logs/test/itemstore/TestLog-ItemStore.log

''' --> Staggering does chop up the per second amount from 16:34:28/29 bins to 35,36,37,38,39,40,41

2026-02-09 17:45:15.078 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'a9ab2d03-6dad-4b65-85fd-57d766014fc1'
2026-02-09 17:45:15.553 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '48cb88f0-768d-48aa-b74d-313f58f80610'
2026-02-09 17:45:15.760 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'f384291a-456c-4f7f-8857-d5a965bdc674'
2026-02-09 17:45:15.988 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '47f3f2ab-ab57-49d2-aa11-c904c7f89df7'
2026-02-09 17:45:17.181 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'ef35d35f-a7c1-4ad5-881d-e5a257026a17'
2026-02-09 17:45:17.205 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '94d42c55-8e18-425b-bf76-ee22a649641e'
2026-02-09 17:45:17.630 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '8c39c757-bc52-46f5-9f45-b9a8bb0d70fa'
2026-02-09 17:45:17.648 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '3b116774-dda8-40db-a3c5-d95ad15e2113'
2026-02-09 17:45:17.759 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'a786ff64-1a3d-4981-8016-a061669b1360'
2026-02-09 17:45:17.998 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '464c20be-5277-4c5b-85ee-b18a9f174750'
2026-02-09 17:45:18.085 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '0253ac65-fc9f-454a-8d5d-7cf5bdc1cf33'
2026-02-09 17:45:18.286 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '86345570-c71f-4fd4-a66c-f9457d2344db'
2026-02-09 17:45:18.784 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '0879d739-fb0e-4617-b9b9-57d15e7bf378'
2026-02-09 17:45:18.868 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '45491ab6-febf-4863-b2de-9b93f9371377'
2026-02-09 17:45:18.963 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'e8b10db2-a579-4a9f-bd88-c3960dcdc677'
2026-02-09 17:45:19.138 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'd0e1e19c-6d52-4f94-91cb-e8abf335e68c'
2026-02-09 17:45:19.557 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'bbe4e6f1-38c9-4fd0-ad98-1a954cb460e3'
2026-02-09 17:45:20.399 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '8b28050c-1570-49e6-9fd3-2ad30d36b673'
2026-02-09 17:45:20.867 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'a9a96b04-46aa-4301-8829-e24b9746d27a'
2026-02-09 17:45:21.035 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '02f8bf50-9826-4d36-b817-62a468d182cb'
2026-02-09 17:45:21.405 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'd9d0d744-1b1c-4b08-ae3b-2fb523ad9ded'
2026-02-09 17:45:21.732 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '25439161-eaf3-4736-830f-32312e9fd37c'
2026-02-09 17:45:22.311 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: 'd2c38fab-1829-4447-bb7d-6e397dba4755'
2026-02-09 17:45:23.013 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '85953363-afba-4448-baf1-db5bef906351'
2026-02-09 17:45:23.225 DEBUG [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.initMutex ]: Initializing mutex: '965dbec7-d82d-474f-b3b5-051fd0c8cbde'

'''


grep "Leadership acquired" logs/test/itemstore/TestLog-ItemStore.log | cat -n

''' --> 28 events, for all 25

     1        2 'bbe4e6f1-38c9-4fd0-ad98-1a954cb460e3'
     2        2 '965dbec7-d82d-474f-b3b5-051fd0c8cbde'
     3        2 '0253ac65-fc9f-454a-8d5d-7cf5bdc1cf33'
     4        1 'f384291a-456c-4f7f-8857-d5a965bdc674'
     5        1 'ef35d35f-a7c1-4ad5-881d-e5a257026a17'

2026-02-09 17:45:17.208 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'a9ab2d03-6dad-4b65-85fd-57d766014fc1'
2026-02-09 17:45:18.555 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '48cb88f0-768d-48aa-b74d-313f58f80610'
2026-02-09 17:45:18.567 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'f384291a-456c-4f7f-8857-d5a965bdc674'
2026-02-09 17:45:19.276 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '47f3f2ab-ab57-49d2-aa11-c904c7f89df7'
2026-02-09 17:45:20.517 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '8c39c757-bc52-46f5-9f45-b9a8bb0d70fa'
2026-02-09 17:45:23.143 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '464c20be-5277-4c5b-85ee-b18a9f174750'
2026-02-09 17:45:25.824 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'e8b10db2-a579-4a9f-bd88-c3960dcdc677'
2026-02-09 17:45:28.520 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'bbe4e6f1-38c9-4fd0-ad98-1a954cb460e3'
2026-02-09 17:45:29.078 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '85953363-afba-4448-baf1-db5bef906351'
2026-02-09 17:45:31.597 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '45491ab6-febf-4863-b2de-9b93f9371377'
2026-02-09 17:45:33.927 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'd0e1e19c-6d52-4f94-91cb-e8abf335e68c'
2026-02-09 17:45:36.944 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'bbe4e6f1-38c9-4fd0-ad98-1a954cb460e3'
2026-02-09 17:45:40.355 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '0879d739-fb0e-4617-b9b9-57d15e7bf378'
2026-02-09 17:45:42.190 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '8b28050c-1570-49e6-9fd3-2ad30d36b673'
2026-02-09 17:45:42.499 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '965dbec7-d82d-474f-b3b5-051fd0c8cbde'
2026-02-09 17:45:44.515 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '965dbec7-d82d-474f-b3b5-051fd0c8cbde'
2026-02-09 17:45:47.540 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'a786ff64-1a3d-4981-8016-a061669b1360'
2026-02-09 17:45:49.371 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'd2c38fab-1829-4447-bb7d-6e397dba4755'
2026-02-09 17:45:51.128 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'd9d0d744-1b1c-4b08-ae3b-2fb523ad9ded'
2026-02-09 17:45:52.354 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'ef35d35f-a7c1-4ad5-881d-e5a257026a17'
2026-02-09 17:45:53.014 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  'a9a96b04-46aa-4301-8829-e24b9746d27a'
2026-02-09 17:45:54.928 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '94d42c55-8e18-425b-bf76-ee22a649641e'
2026-02-09 17:45:56.591 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '25439161-eaf3-4736-830f-32312e9fd37c'
2026-02-09 17:45:57.298 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '02f8bf50-9826-4d36-b817-62a468d182cb'
2026-02-09 17:45:57.852 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '0253ac65-fc9f-454a-8d5d-7cf5bdc1cf33'
2026-02-09 17:45:59.707 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '3b116774-dda8-40db-a3c5-d95ad15e2113'
2026-02-09 17:46:00.008 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '86345570-c71f-4fd4-a66c-f9457d2344db'
2026-02-09 17:46:01.814 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership acquired:  '0253ac65-fc9f-454a-8d5d-7cf5bdc1cf33'
'''



grep "Leadership verified" logs/test/itemstore/TestLog-ItemStore.log
grep "File Channel Lock release" logs/test/itemstore/TestLog-ItemStore.log | grep "id" | wc -l

''' --> Leadership verified for all 25, only 24 locks released

2026-02-09 17:45:17.216 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  'a9ab2d03-6dad-4b65-85fd-57d766014fc1'
2026-02-09 17:45:18.558 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  '48cb88f0-768d-48aa-b74d-313f58f80610'
2026-02-09 17:45:18.574 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  'f384291a-456c-4f7f-8857-d5a965bdc674'
2026-02-09 17:45:19.283 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  '47f3f2ab-ab57-49d2-aa11-c904c7f89df7'
2026-02-09 17:45:20.882 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  '8c39c757-bc52-46f5-9f45-b9a8bb0d70fa'
2026-02-09 17:45:23.423 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  '464c20be-5277-4c5b-85ee-b18a9f174750'
2026-02-09 17:45:26.215 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  'e8b10db2-a579-4a9f-bd88-c3960dcdc677'
2026-02-09 17:45:29.393 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  '85953363-afba-4448-baf1-db5bef906351'
2026-02-09 17:45:31.924 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  '45491ab6-febf-4863-b2de-9b93f9371377'
2026-02-09 17:45:34.300 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  'd0e1e19c-6d52-4f94-91cb-e8abf335e68c'
2026-02-09 17:45:37.402 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  'bbe4e6f1-38c9-4fd0-ad98-1a954cb460e3'
2026-02-09 17:45:40.567 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  '0879d739-fb0e-4617-b9b9-57d15e7bf378'
2026-02-09 17:45:42.453 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  '8b28050c-1570-49e6-9fd3-2ad30d36b673'
2026-02-09 17:45:44.924 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  '965dbec7-d82d-474f-b3b5-051fd0c8cbde'
2026-02-09 17:45:47.810 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  'a786ff64-1a3d-4981-8016-a061669b1360'
2026-02-09 17:45:49.605 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  'd2c38fab-1829-4447-bb7d-6e397dba4755'
2026-02-09 17:45:51.323 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  'd9d0d744-1b1c-4b08-ae3b-2fb523ad9ded'
2026-02-09 17:45:52.462 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  'ef35d35f-a7c1-4ad5-881d-e5a257026a17'
2026-02-09 17:45:53.185 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  'a9a96b04-46aa-4301-8829-e24b9746d27a'
2026-02-09 17:45:55.160 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  '94d42c55-8e18-425b-bf76-ee22a649641e'
2026-02-09 17:45:56.675 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  '25439161-eaf3-4736-830f-32312e9fd37c'
2026-02-09 17:45:57.782 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  '02f8bf50-9826-4d36-b817-62a468d182cb'
2026-02-09 17:45:59.750 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  '3b116774-dda8-40db-a3c5-d95ad15e2113'
2026-02-09 17:46:00.165 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  '86345570-c71f-4fd4-a66c-f9457d2344db'
2026-02-09 17:46:01.913 INFO  [ main -> org.tasktide.itemstore.mutex.strategy.ElectionStrategy.waitUntilLeader ]: Leadership verified:  '0253ac65-fc9f-454a-8d5d-7cf5bdc1cf33'

'''



grep "Leadership confirmed" logs/test/itemstore/TestLog-ItemStore.log

''' ---> Leadership confirmed for 24/25

Drop off
f384291a-456c-4f7f-8857-d5a965bdc674 -> No retry from 

2026-02-09 17:45:18.687 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Confirming leadership:    'f384291a-456c-4f7f-8857-d5a965bdc674'
2026-02-09 17:45:18.701 DEBUG [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.cleanUp ]: Cleaning up lock file:        'f384291a-456c-4f7f-8857-d5a965bdc674'
2026-02-09 17:45:18.723 DEBUG [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.cleanUp ]: Cleaning up host file:        'f384291a-456c-4f7f-8857-d5a965bdc674'
2026-02-09 17:45:18.726 DEBUG [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.cleanUp ]: Cleaning up confirm ballot file:      'f384291a-456c-4f7f-8857-d5a965bdc674'
2026-02-09 17:45:18.728 DEBUG [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.cleanUp ]: Cleaning up election file:    'f384291a-456c-4f7f-8857-d5a965bdc674'
Unable to confirm NFS mutex:    f384291a-456c-4f7f-8857-d5a965bdc674
2026-02-09 17:45:18.729 WARN  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.tryAcquireUntilSuccess ]: Lock acquisition failed, retrying:

2026-02-09 17:45:18.017 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'a9ab2d03-6dad-4b65-85fd-57d766014fc1'
2026-02-09 17:45:18.785 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '48cb88f0-768d-48aa-b74d-313f58f80610'
2026-02-09 17:45:19.614 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '47f3f2ab-ab57-49d2-aa11-c904c7f89df7'
2026-02-09 17:45:21.870 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '8c39c757-bc52-46f5-9f45-b9a8bb0d70fa'
2026-02-09 17:45:24.132 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '464c20be-5277-4c5b-85ee-b18a9f174750'
2026-02-09 17:45:27.204 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'e8b10db2-a579-4a9f-bd88-c3960dcdc677'
2026-02-09 17:45:30.148 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '85953363-afba-4448-baf1-db5bef906351'
2026-02-09 17:45:32.502 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '45491ab6-febf-4863-b2de-9b93f9371377'
2026-02-09 17:45:35.118 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'd0e1e19c-6d52-4f94-91cb-e8abf335e68c'
2026-02-09 17:45:38.371 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'bbe4e6f1-38c9-4fd0-ad98-1a954cb460e3'
2026-02-09 17:45:41.073 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '0879d739-fb0e-4617-b9b9-57d15e7bf378'
2026-02-09 17:45:43.383 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '8b28050c-1570-49e6-9fd3-2ad30d36b673'
2026-02-09 17:45:45.759 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '965dbec7-d82d-474f-b3b5-051fd0c8cbde'
2026-02-09 17:45:48.188 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'a786ff64-1a3d-4981-8016-a061669b1360'
2026-02-09 17:45:50.075 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'd2c38fab-1829-4447-bb7d-6e397dba4755'
2026-02-09 17:45:51.695 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'd9d0d744-1b1c-4b08-ae3b-2fb523ad9ded'
2026-02-09 17:45:52.791 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'ef35d35f-a7c1-4ad5-881d-e5a257026a17'
2026-02-09 17:45:53.669 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     'a9a96b04-46aa-4301-8829-e24b9746d27a'
2026-02-09 17:45:55.353 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '94d42c55-8e18-425b-bf76-ee22a649641e'
2026-02-09 17:45:56.842 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '25439161-eaf3-4736-830f-32312e9fd37c'
2026-02-09 17:45:58.564 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '02f8bf50-9826-4d36-b817-62a468d182cb'
2026-02-09 17:45:59.920 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '3b116774-dda8-40db-a3c5-d95ad15e2113'
2026-02-09 17:46:00.480 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '86345570-c71f-4fd4-a66c-f9457d2344db'
2026-02-09 17:46:02.124 INFO  [ main -> org.tasktide.itemstore.mutex.MutexOrchestrator.performLock ]: Leadership confirmed:     '0253ac65-fc9f-454a-8d5d-7cf5bdc1cf33'

'''