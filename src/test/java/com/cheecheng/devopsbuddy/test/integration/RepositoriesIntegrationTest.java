package com.cheecheng.devopsbuddy.test.integration;

import com.cheecheng.devopsbuddy.DevopsbuddyApplication;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.Role;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.User;
import com.cheecheng.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.cheecheng.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.cheecheng.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.cheecheng.devopsbuddy.enums.PlansEnum;
import com.cheecheng.devopsbuddy.enums.RolesEnum;
import com.cheecheng.devopsbuddy.utils.UserUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class RepositoriesIntegrationTest {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void init() {
        Assert.assertNotNull(planRepository);
        Assert.assertNotNull(roleRepository);
        Assert.assertNotNull(userRepository);
    }

    @Test
    public void testCreateNewPlan() {
        Plan basicPlan = createPlan(PlansEnum.BASIC);
        planRepository.save(basicPlan);

        Plan retrievedPlan = planRepository.findOne(PlansEnum.BASIC.getId());
        Assert.assertNotNull(retrievedPlan);
    }

    @Test
    public void testCreateNewRole() {
        Role userRole = createRole(RolesEnum.BASIC);
        roleRepository.save(userRole);

        Role retrievedRole = roleRepository.findOne(RolesEnum.BASIC.getId());
        Assert.assertNotNull(retrievedRole);
    }

    @Test
    public void testCreateNewUser() {

        User basicUser = createUser();

        User newlyCreatedUser = userRepository.findOne(basicUser.getId());

        // If all relationships contain data after running the findOne() method,
        // it means our Repositories work correctly
        Assert.assertNotNull(newlyCreatedUser);
        Assert.assertTrue(newlyCreatedUser.getId() != 0);
        Assert.assertNotNull(newlyCreatedUser.getPlan());
        Assert.assertNotNull(newlyCreatedUser.getPlan().getId());
        Set<Role> newlyCreatedUserRoles = newlyCreatedUser.getRoles();
        for (Role role : newlyCreatedUserRoles) {
            Assert.assertNotNull(role);
            Assert.assertNotNull(role.getId());
        }
    }

    /*
    Need @After tearDown() method to clean the database after EACH test, or will get the error below
    because of existing data in the database.

    14712 [main] WARN  o.h.e.jdbc.spi.SqlExceptionHelper - SQL Error: 23505, SQLState: 23505
    14715 [main] ERROR o.h.e.jdbc.spi.SqlExceptionHelper - Unique index or primary key violation:
    "PRIMARY KEY ON PUBLIC.ROLE(ID)"; SQL statement: insert into role (name, id) values (?, ?) [23505-196]

    org.springframework.dao.DataIntegrityViolationException: could not execute statement; SQL [n/a];
    constraint ["PRIMARY KEY ON PUBLIC.ROLE(ID)"; SQL statement: insert into role (name, id) values (?, ?) [23505-196]];
    nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement

    at com.cheecheng.devopsbuddy.test.integration.RepositoriesIntegrationTest.testCreateNewUser(RepositoriesIntegrationTest.java:78)

	Caused by: org.h2.jdbc.JdbcSQLException: Unique index or primary key violation: "PRIMARY KEY ON PUBLIC.ROLE(ID)";
	SQL statement: insert into role (name, id) values (?, ?) [23505-196]
     */

    @Test
    public void testDeleteUser() {
        User basicUser = createUser();
        userRepository.delete(basicUser.getId());
    }

    @After
    public void tearDown() throws Exception {
        /*
        Need to delete users first, or will get the following error:
        17676 [main] ERROR o.h.e.jdbc.spi.SqlExceptionHelper - Referential integrity constraint violation:
        "FKEOS0C7NC1MVICJCXBKXXOLOHC: PUBLIC.USER FOREIGN KEY(PLAN_ID) REFERENCES PUBLIC.PLAN(ID) (1)";
        SQL statement: delete from plan where id=? [23503-196]

        org.springframework.dao.DataIntegrityViolationException: could not execute statement; SQL [n/a];
        constraint ["FKEOS0C7NC1MVICJCXBKXXOLOHC: PUBLIC.USER FOREIGN KEY(PLAN_ID) REFERENCES PUBLIC.PLAN(ID) (1)";
        SQL statement: delete from plan where id=? [23503-196]];
        nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement
        at com.cheecheng.devopsbuddy.test.integration.RepositoriesIntegrationTest.tearDown(RepositoriesIntegrationTest.java:97)

	    Caused by: org.h2.jdbc.JdbcSQLException: Referential integrity constraint violation:
	    "FKEOS0C7NC1MVICJCXBKXXOLOHC: PUBLIC.USER FOREIGN KEY(PLAN_ID) REFERENCES PUBLIC.PLAN(ID) (1)";
	    SQL statement: delete from plan where id=? [23503-196]
         */
        userRepository.deleteAll();
        planRepository.deleteAll();
        roleRepository.deleteAll();
    }

    private Plan createPlan(PlansEnum plansEnum) {
        return new Plan(plansEnum);
    }

    private Role createRole(RolesEnum rolesEnum) {
        return new Role(rolesEnum);
    }

    private User createUser() {
        Plan basicPlan = createPlan(PlansEnum.BASIC);
        planRepository.save(basicPlan);

        User basicUser = UserUtils.createBasicUser();
        basicUser.setPlan(basicPlan);

        Role basicRole = createRole(RolesEnum.BASIC);
        //roleRepository.save(basicRole);
        // DO NOT CALL roleRepository.save(basicRole);
        // Will receive error:
        // org.springframework.dao.DataIntegrityViolationException: could not execute statement; SQL [n/a];
        // constraint ["PRIMARY KEY ON PUBLIC.ROLE(ID)"; SQL statement:
        // insert into role (name, id) values (?, ?) [23505-196]];
        // nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement
        // Caused by: org.h2.jdbc.JdbcSQLException: Unique index or primary key violation: "PRIMARY KEY ON PUBLIC.ROLE(ID)";
        // SQL statement: insert into role (name, id) values (?, ?) [23505-196]
        // **** Because roleRepository.save(basicRole) will be called by userRepository.save(basicUser); ****

        basicUser.addRole(basicRole);

        basicUser = userRepository.save(basicUser);

        return basicUser;
    }
}

/*
With User.java:
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
                fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

userRepository.findOne(basicUser.getId()) generated this Hibernate SQL:
    select
        user0_.id as id1_2_0_,
        user0_.country as country2_2_0_,
        user0_.description as descript3_2_0_,
        user0_.email as email4_2_0_,
        user0_.enabled as enabled5_2_0_,
        user0_.first_name as first_na6_2_0_,
        user0_.last_name as last_nam7_2_0_,
        user0_.password as password8_2_0_,
        user0_.phone_number as phone_nu9_2_0_,
        user0_.plan_id as plan_id13_2_0_,
        user0_.profile_image_url as profile10_2_0_,
        user0_.stripe_customer_id as stripe_11_2_0_,
        user0_.username as usernam12_2_0_,
        plan1_.id as id1_0_1_,
        plan1_.name as name2_0_1_,
-->        roles2_.users_id as users_id1_3_2_,
-->        role3_.id as roles_id2_3_2_,
-->        role3_.id as id1_1_3_,
-->        role3_.name as name2_1_3_
    from
        user user0_
    left outer join
        plan plan1_
            on user0_.plan_id=plan1_.id
-->    left outer join
-->        user_roles roles2_
-->            on user0_.id=roles2_.users_id
-->    left outer join
-->        role role3_
-->            on roles2_.roles_id=role3_.id
    where
        user0_.id=?

With User.java:
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Role> roles = new HashSet<>();
userRepository.findOne(basicUser.getId()) generated this Hibernate SQL:
    select
        user0_.id as id1_2_0_,
        user0_.country as country2_2_0_,
        user0_.description as descript3_2_0_,
        user0_.email as email4_2_0_,
        user0_.enabled as enabled5_2_0_,
        user0_.first_name as first_na6_2_0_,
        user0_.last_name as last_nam7_2_0_,
        user0_.password as password8_2_0_,
        user0_.phone_number as phone_nu9_2_0_,
        user0_.plan_id as plan_id13_2_0_,
        user0_.profile_image_url as profile10_2_0_,
        user0_.stripe_customer_id as stripe_11_2_0_,
        user0_.username as usernam12_2_0_,
        plan1_.id as id1_0_1_,
        plan1_.name as name2_0_1_
    from
        user user0_
    left outer join
        plan plan1_
            on user0_.plan_id=plan1_.id
    where
        user0_.id=?

This SQL causes the test failed with the following exception,
org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: com.cheecheng.devopsbuddy.backend.persistence.domain.backend.User.roles, could not initialize proxy - no Session

	at org.hibernate.collection.internal.AbstractPersistentCollection.throwLazyInitializationException(AbstractPersistentCollection.java:587)
	at org.hibernate.collection.internal.AbstractPersistentCollection.withTemporarySessionIfNeeded(AbstractPersistentCollection.java:204)
	at org.hibernate.collection.internal.AbstractPersistentCollection.initialize(AbstractPersistentCollection.java:566)
	at org.hibernate.collection.internal.AbstractPersistentCollection.read(AbstractPersistentCollection.java:135)
	at org.hibernate.collection.internal.PersistentSet.iterator(PersistentSet.java:163)
	at com.cheecheng.devopsbuddy.test.integration.RepositoriesIntegrationTest.testCreateNewUser(RepositoriesIntegrationTest.java:87)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:26)

Note that the SQL generated with "fetch = FetchType.EAGER" has extra items,
-->        roles2_.users_id as users_id1_3_2_,
-->        role3_.id as roles_id2_3_2_,
-->        role3_.id as id1_1_3_,
-->        role3_.name as name2_1_3_

-->    left outer join
-->        user_roles roles2_
-->            on user0_.id=roles2_.users_id
-->    left outer join
-->        role role3_
-->            on roles2_.roles_id=role3_.id

Test successful with "fetch = FetchType.EAGER"
 */