这是一个springboot整合redis并且使用springboot-data-redis来操作reids的demo。redis运行在ubuntu中的docker上


在该demo中，使用了缓存注解的方式来管理缓存。并且对非注解方式的也做了演示，
关于分布式锁，本例子中的setNx，由于没有将超时一起管理，所以任然会有线程安全的问题。jedis高版本有set方法，他是在setNx方法上多处理了超时，所以线程安全
