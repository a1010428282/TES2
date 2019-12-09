package top.jach.tes.dev.app;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import top.jach.tes.core.api.domain.Project;
import top.jach.tes.core.api.domain.info.Info;
import top.jach.tes.core.api.factory.InfoRepositoryFactory;
import top.jach.tes.core.api.repository.InfoRepository;
import top.jach.tes.core.easy.Environment;
import top.jach.tes.core.impl.domain.context.BaseContextFactory;
import top.jach.tes.core.impl.factory.DefaultInfoRepositoryFactory;
import top.jach.tes.core.impl.matching.DefaultNToOneMatchingStrategy;
import top.jach.tes.core.impl.matching.NToOneMatchingStrategy;
import top.jach.tes.core.impl.service.DefaultInfoService;
import top.jach.tes.plugin.tes.repository.GeneraInfoMongoRepository;

import java.util.Date;
import java.util.Set;

public abstract class DevApp {
    private static boolean inited = false;
    static {
        init();
    }
    public static void init() {
        if(inited){
            return;
        }
        Environment.infoRepositoryFactory = infoRepositoryFactory();
        Environment.contextFactory = new BaseContextFactory(Environment.iLoggerFactory, Environment.infoRepositoryFactory);
        Environment.infoService = new DefaultInfoService(Environment.contextFactory);
        Environment.defaultProject = new Project().setName("DevProject").setDesc("project for dev");
        Environment.defaultProject.setId(1l).setCreatedTime(1575784638000l).setUpdatedTime(1575784638000l);
    }
    private static InfoRepositoryFactory infoRepositoryFactory(){
        DefaultInfoRepositoryFactory factory = new DefaultInfoRepositoryFactory();
        DefaultNToOneMatchingStrategy<Class<? extends Info>, InfoRepository> strategy = new DefaultNToOneMatchingStrategy<>();
        factory.register(new NToOneMatchingStrategy<Class<? extends Info>, InfoRepository>() {
            @Override
            public InfoRepository NToM(Class<? extends Info> aClass) {
                MongoClient mongoClient = new MongoClient();
                MongoCollection mongoCollection = mongoClient.getDatabase("tes_dev").getCollection("general_info");
                return new GeneraInfoMongoRepository(mongoCollection);
            }

            @Override
            public Set<Class<? extends Info>> MToN(InfoRepository infoRepository) {
                return null;
            }
        });
        return factory;
    }
}