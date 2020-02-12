package com.delfood.mapper;

import com.delfood.dto.rider.RiderDTO;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface RiderInfoMapper {
  
  public boolean isExistById(@NonNull String id);

  public void insertRider(@NonNull RiderDTO riderInfo);

  public RiderDTO findByIdAndPassword(@NonNull String id, @NonNull String password);

  public long updatePassword(@NonNull String id, @NonNull String password);

  public long updateStatusAsDeleted(@NonNull String id);
  
  public boolean isExistAndEffectiveByIdAndPassword(@NonNull String id,
      @NonNull String password);

  public long updateMail(@NonNull String id, @NonNull String mail);

  public boolean isRiderOrder(@NonNull String riderId, @NonNull Long orderId);

  public boolean hasDelivery(String riderId);
}
