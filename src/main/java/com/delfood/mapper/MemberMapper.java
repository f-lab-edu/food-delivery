package com.delfood.mapper;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.delfood.dto.MemberDTO;

@Repository
public interface MemberMapper {

  public MemberDTO findById(String id);

  public int insertMember(MemberDTO memberInfo);

  public MemberDTO findByIdAndPassword(String id, String password);

  public int updateMemberPassword(String id, String password);

  public int deleteMember(String id);

  public int updateMemberAddress(String id, String address, String addressDetail);

  public int idCheck(String id);
  
}
