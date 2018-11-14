package org.passwordtree.config;

import ma.glasnost.orika.Converter;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrikaBeanMapper extends ConfigurableMapper implements ApplicationContextAware {

    private MapperFactory mapperFactory;
    private ApplicationContext applicationContext;

    public OrikaBeanMapper() {
        super(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure(MapperFactory factory) {
        this.mapperFactory = factory;
        addAllSpringBeans(applicationContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureFactoryBuilder(final DefaultMapperFactory.Builder factoryBuilder) {
        // Nothing to configure for now
    }

    /**
     * Constructs and registers a {@link ma.glasnost.orika.metadata.ClassMapBuilder} into the {@link MapperFactory} using a {@link Mapper}.
     *
     * @param mapper
     */
    @SuppressWarnings("rawtypes")
    public void addMapper(Mapper<?, ?> mapper) {
        /*
         * Configuration goes here i.e.
         * if(mapper instance of XyMapper) {
         *   mapperFactory.classMap(mapper.getAType(), mapper.getBType())
         *       .field("fieldNameA", "fieldNameB") // only needed if fieldnames don't match exactly
         *       .exclude("exampleRelation")
         *       .byDefault()
         *       .customize((Mapper) mapper)
         *       .register();
         * }
         *
         * */
    }

    /**
     * Registers a {@link Converter} into the {@link ma.glasnost.orika.converter.ConverterFactory}.
     *
     * @param converter
     */
    public void addConverter(Converter<?, ?> converter) {
        mapperFactory.getConverterFactory().registerConverter(converter);
    }


    /**
     * Scans the appliaction context and registers all Mappers and Converters found in it.
     *
     * @param applicationContext
     */
    @SuppressWarnings("rawtypes")
    private void addAllSpringBeans(final ApplicationContext applicationContext) {
        Map<String, Mapper> mappers = applicationContext.getBeansOfType(Mapper.class);
        for (Mapper mapper : mappers.values()) {
            addMapper(mapper);
        }
        Map<String, Converter> converters = applicationContext.getBeansOfType(Converter.class);
        for (Converter converter : converters.values()) {
            addConverter(converter);
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        init();
    }

}
