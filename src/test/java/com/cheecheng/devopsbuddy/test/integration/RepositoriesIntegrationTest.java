package com.cheecheng.devopsbuddy.test.integration;

import com.cheecheng.devopsbuddy.DevopsbuddyApplication;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.Role;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.User;
import com.cheecheng.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.cheecheng.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.cheecheng.devopsbuddy.backend.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
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

    private static final int BASIC_PLAN_ID = 1;
    private static final int BASIC_ROLE_ID = 1;

    @Before
    public void init() {
        Assert.assertNotNull(planRepository);
        Assert.assertNotNull(roleRepository);
        Assert.assertNotNull(userRepository);
    }

    @Test
    public void testCreateNewPlan() {
        Plan basicPlan = createBasicPlan();
        planRepository.save(basicPlan);

        Plan retrievedPlan = planRepository.findOne(BASIC_PLAN_ID);
        Assert.assertNotNull(retrievedPlan);
    }

    @Test
    public void testCreateNewRole() {
        Role userRole = createBasicRole();
        roleRepository.save(userRole);

        Role retrievedRole = roleRepository.findOne(BASIC_ROLE_ID);
        Assert.assertNotNull(retrievedRole);
    }

    @Test
    public void testCreateNewUser() {

        // Create a save a Plan record
        Plan basicPlan = createBasicPlan();
        planRepository.save(basicPlan);

        // Create User instance and set the Plan saved entity as Foreign Key
        User basicUser = createBasicUser();
        basicUser.setPlan(basicPlan);

        Role basicRole = createBasicRole();
        basicUser.addRole(basicRole);

        basicUser = userRepository.save(basicUser);
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

    private Plan createBasicPlan() {
        Plan plan = new Plan();
        plan.setId(BASIC_PLAN_ID);
        plan.setName("Basic");
        return plan;
    }

    private Role createBasicRole() {
        Role role = new Role();
        role.setId(BASIC_ROLE_ID);
        role.setName("ROLE_USER");
        return role;
    }

    private User createBasicUser() {
        User user = new User();
        user.setUsername("basicUser");
        user.setPassword("secret");
        user.setEmail("me@example.com");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPhoneNumber("123456789123");
        user.setCountry("GB");
        user.setEnabled(true);
        user.setDescription("A basic user");
        user.setProfileImageUrl("https://blabla.images.com/basicuser");

        return user;
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