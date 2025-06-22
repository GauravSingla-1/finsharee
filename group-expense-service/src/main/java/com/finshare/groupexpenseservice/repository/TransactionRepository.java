package com.finshare.groupexpenseservice.repository;

import com.finshare.groupexpenseservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Transaction entities.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    /**
     * Find all transactions for a specific expense.
     *
     * @param expenseId The expense ID
     * @return List of transactions for the expense
     */
    List<Transaction> findByExpenseId(String expenseId);

    /**
     * Find all transactions for a specific group.
     *
     * @param groupId The group ID
     * @return List of transactions for the group
     */
    List<Transaction> findByGroupId(String groupId);

    /**
     * Find all transactions where a user owes money (as fromUser).
     *
     * @param userId The user ID
     * @return List of transactions where the user owes money
     */
    List<Transaction> findByFromUserId(String userId);

    /**
     * Find all transactions where a user is owed money (as toUser).
     *
     * @param userId The user ID
     * @return List of transactions where the user is owed money
     */
    List<Transaction> findByToUserId(String userId);

    /**
     * Find all transactions between two specific users.
     *
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return List of transactions between the two users
     */
    @Query("SELECT t FROM Transaction t WHERE (t.fromUserId = :userId1 AND t.toUserId = :userId2) OR (t.fromUserId = :userId2 AND t.toUserId = :userId1)")
    List<Transaction> findTransactionsBetweenUsers(@Param("userId1") String userId1, @Param("userId2") String userId2);

    /**
     * Delete all transactions for a specific expense.
     *
     * @param expenseId The expense ID
     */
    void deleteByExpenseId(String expenseId);
}