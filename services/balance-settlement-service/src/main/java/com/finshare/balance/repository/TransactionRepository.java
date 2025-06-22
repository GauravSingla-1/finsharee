package com.finshare.balance.repository;

import com.finshare.balance.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Transaction entity operations.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    /**
     * Find all unsettled transactions where a user is involved (either as payer or recipient).
     */
    @Query("SELECT t FROM Transaction t WHERE (t.fromUserId = :userId OR t.toUserId = :userId) AND t.isSettled = false")
    List<Transaction> findUnsettledTransactionsByUserId(@Param("userId") String userId);

    /**
     * Find all unsettled transactions within a specific group.
     */
    @Query("SELECT t FROM Transaction t WHERE t.groupId = :groupId AND t.isSettled = false")
    List<Transaction> findUnsettledTransactionsByGroupId(@Param("groupId") String groupId);

    /**
     * Find all unsettled transactions where a user owes money to others.
     */
    @Query("SELECT t FROM Transaction t WHERE t.fromUserId = :userId AND t.isSettled = false")
    List<Transaction> findUnsettledDebtsByUserId(@Param("userId") String userId);

    /**
     * Find all unsettled transactions where others owe money to a user.
     */
    @Query("SELECT t FROM Transaction t WHERE t.toUserId = :userId AND t.isSettled = false")
    List<Transaction> findUnsettledCreditsByUserId(@Param("userId") String userId);

    /**
     * Find specific unsettled transactions between two users in a group.
     */
    @Query("SELECT t FROM Transaction t WHERE t.groupId = :groupId AND t.fromUserId = :fromUserId AND t.toUserId = :toUserId AND t.isSettled = false ORDER BY t.createdAt ASC")
    List<Transaction> findUnsettledTransactionsBetweenUsers(
        @Param("groupId") String groupId,
        @Param("fromUserId") String fromUserId,
        @Param("toUserId") String toUserId
    );

    /**
     * Count total number of transactions for a user.
     */
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.fromUserId = :userId OR t.toUserId = :userId")
    long countTransactionsByUserId(@Param("userId") String userId);
}