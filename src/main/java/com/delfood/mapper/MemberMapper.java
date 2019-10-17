package com.delfood.mapper;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.delfood.dto.MemberDTO;

@Repository
public interface MemberMapper {

  MemberDTO findById(String id);

  int insertMember(MemberDTO memberInfo);

  MemberDTO findByIdAndPassword(String id, String password);

  int updateMemberPassword(String id, String password);

  int deleteMember(String id);

  int updateMemberAddress(String id, String address, String addressDetail);

  int idCheck(String id);
}
