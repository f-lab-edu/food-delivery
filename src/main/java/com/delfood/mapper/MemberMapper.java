package com.delfood.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.delfood.dto.member.MemberDTO;

@Repository
public interface MemberMapper {

  public MemberDTO findById(String id);

  public int insertMember(MemberDTO memberInfo);

  public MemberDTO findByIdAndPassword(@Param("id") String id, @Param("password") String password);

  public int updateMemberPassword(String id, String password);

  public int deleteMember(String id);

  int updateMemberAddress(@Param("id") String id, @Param("addressCode") String addressCode, @Param("addressDetail") String addressDetail);

  int idCheck(String id);

  String findTownCodeById(String id);
  
}
