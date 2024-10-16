package io.openbas.database.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.openbas.database.model.Base;
import jakarta.annotation.Resource;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ModelBaseListener {

  public static final String DATA_PERSIST = "DATA_FETCH_SUCCESS";
  public static final String DATA_UPDATE = "DATA_UPDATE_SUCCESS";
  public static final String DATA_DELETE = "DATA_DELETE_SUCCESS";

  @Resource protected ObjectMapper mapper;

  private ApplicationEventPublisher appPublisher;

  @Autowired
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.appPublisher = applicationEventPublisher;
  }

  @PostPersist
  void postPersist(Object base) {
    Base instance = (Base) base;
    BaseEvent event = new BaseEvent(DATA_PERSIST, instance, mapper);
    appPublisher.publishEvent(event);
  }

  @PostUpdate
  void postUpdate(Object base) {
    Base instance = (Base) base;
    BaseEvent event = new BaseEvent(DATA_UPDATE, instance, mapper);
    appPublisher.publishEvent(event);
  }

  @PreRemove
  void preRemove(Object base) {
    Base instance = (Base) base;
    appPublisher.publishEvent(new BaseEvent(DATA_DELETE, instance, mapper));
  }
}
