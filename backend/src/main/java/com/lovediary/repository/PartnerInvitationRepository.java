package com.lovediary.repository;

import com.lovediary.entity.PartnerInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartnerInvitationRepository extends JpaRepository<PartnerInvitation, Long> {

    @Query("SELECT pi FROM PartnerInvitation pi WHERE pi.toUserId = :userId AND pi.status = 'PENDING'")
    List<PartnerInvitation> findPendingInvitationsByToUserId(@Param("userId") Long userId);

    @Query("SELECT pi FROM PartnerInvitation pi WHERE pi.fromUserId = :userId AND pi.toUserId = :toUserId AND pi.status = 'PENDING'")
    Optional<PartnerInvitation> findPendingInvitationByFromAndToUser(@Param("userId") Long userId, @Param("toUserId") Long toUserId);

    @Query("SELECT pi FROM PartnerInvitation pi WHERE (pi.fromUserId = :userId OR pi.toUserId = :userId) AND pi.status = 'PENDING'")
    List<PartnerInvitation> findPendingInvitationsByUserId(@Param("userId") Long userId);

    @Query("SELECT pi FROM PartnerInvitation pi WHERE pi.fromUserId = :fromUserId AND pi.toUserId = :toUserId")
    Optional<PartnerInvitation> findByFromUserIdAndToUserId(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);

    @Query("SELECT pi FROM PartnerInvitation pi WHERE pi.fromUserId = :userId AND pi.status = 'PENDING'")
    List<PartnerInvitation> findPendingInvitationsByFromUserId(@Param("userId") Long userId);

    @Query("SELECT pi FROM PartnerInvitation pi WHERE pi.fromUserId = :userId1 OR pi.fromUserId = :userId2 OR pi.toUserId = :userId1 OR pi.toUserId = :userId2")
    List<PartnerInvitation> findByFromUserIdOrToUserId(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
