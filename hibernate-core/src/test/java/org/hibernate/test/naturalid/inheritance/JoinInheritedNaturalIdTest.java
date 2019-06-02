package org.hibernate.test.naturalid.inheritance;

import org.hibernate.annotations.NaturalId;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;

import javax.persistence.*;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;

public class JoinInheritedNaturalIdTest extends BaseEntityManagerFunctionalTestCase {
    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class<?>[] {
                Post.class,
                TimeCode.class,
                TaskCode.class
        };
    }

    @Test
    public void testCreation() {
        doInJPA( this::entityManagerFactory, entityManager -> {
            TaskCode code = new TaskCode();
            code.setCode("001");

            entityManager.persist(code);

            Post post = new Post();
            post.setTask(code);

            entityManager.persist(post);
        });
    }

    @Entity(name = "Post")
    public static class Post {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "task", referencedColumnName = "code")
        public TaskCode task;

        public TaskCode getTask() {
            return task;
        }

        public void setTask(TaskCode task) {
            this.task = task;
        }
    }

    @Entity
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorColumn(name="type", discriminatorType = DiscriminatorType.STRING)
    public abstract class TimeCode {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @NaturalId
        @Column(name = "code", unique = true, nullable = false)
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    @Entity
    @DiscriminatorValue("task")
    public class TaskCode extends TimeCode {
        public TaskCode() {
        }

        public TaskCode(String code) {
            this.setCode(code);
        }
    }
}
