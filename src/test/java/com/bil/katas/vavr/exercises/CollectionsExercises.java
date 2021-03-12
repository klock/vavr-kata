package com.bil.katas.vavr.exercises;

import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.bil.katas.vavr.exercises.PetType.*;

/**
 * By doing these exercises you should have learned about the following APIs.
 * <p/>
 * {@link Seq#map(Function)}<br>
 * {@link Seq#filter(Predicate)}<br>
 * {@link Seq#reject(Predicate)}<br>
 * {@link Seq#find(Predicate)}<br>
 * {@link Seq#forAll(Predicate)}<br>
 * {@link Seq#count(Predicate)}<br>
 * {@link Seq#map(Function)}<br>
 * {@link Seq#flatMap(Function)}<br>
 * <p>
 * {@link Seq#count(Predicate)}<br>
 * <p>
 */
public class CollectionsExercises extends PetDomainKata {

    public static final Predicate<Person> IS_PERSON_WITH_CAT = person -> person.hasPetType(CAT);
    public static final Predicate<Person> IS_PET_PERSON = Person::isPetPerson;

    @Test
    public void getFirstNamesOfAllPeople() {
        // Replace null, with a transformation method on Seq.
        Seq<String> firstNames = people.map(Person::getFirstName);

        Seq<String> expectedFirstNames = Vector.of("Mary", "Bob", "Ted", "Jake", "Barry", "Terry", "Harry", "John");
        Assert.assertEquals(expectedFirstNames, firstNames);
    }

    @Test
    public void getNamesOfMarySmithsPets() {
        Person person = this.getPersonNamed("Mary Smith");
        Seq<Pet> pets = person.getPets();

        // Replace null, with a transformation method on Seq.
        Seq<String> names = pets.map(Pet::getName);

        Assert.assertEquals("Tabby", names.mkString());
    }

    @Test
    public void getPeopleWithCats() {
        // Replace null, with a positive filtering method on Seq.
        Seq<Person> peopleWithCats = people.filter(Person::isPetPerson).filter(IS_PERSON_WITH_CAT);

        Assert.assertThat(peopleWithCats.size(), CoreMatchers.equalTo(2));
    }

    @Test
    public void getPeopleWithoutCats() {
        // Replace null, with a negative filtering method on Seq.
        Seq<Person> peopleWithoutCats = people.reject(IS_PERSON_WITH_CAT);

        Assert.assertThat(peopleWithoutCats.size(), CoreMatchers.equalTo(6));
    }

    @Test
    public void doAnyPeopleHaveCats() {
        //replace null with a Predicate lambda which checks for PetType.CAT
        boolean doAnyPeopleHaveCats = people.find(IS_PERSON_WITH_CAT).isDefined();
        Assert.assertTrue(doAnyPeopleHaveCats);
    }

    @Test
    public void doAllPeopleHavePets() {
        //replace with a method call send to this.people that checks if all people have pets
        boolean result = people.forAll(IS_PET_PERSON);
        Assert.assertFalse(result);
    }

    @Test
    public void howManyPeopleHaveCats() {
        // replace 0 with the correct answer
        int count = people.count(IS_PERSON_WITH_CAT);
        Assert.assertEquals(2, count);
    }

    @Test
    public void findMarySmith() {
        Person result = this.getPersonNamed("Mary Smith");
        Assert.assertEquals("Mary", result.getFirstName());
        Assert.assertEquals("Smith", result.getLastName());
    }

    @Test
    public void getPeopleWithPets() {
        // replace with only the pets owners
        Seq<Person> petPeople = people.filter(IS_PET_PERSON);
        Assert.assertThat(petPeople.size(), CoreMatchers.equalTo(7));
    }

    @Test
    public void getAllPetTypesOfAllPeople() {
        // retrieve all pet types owned by the people
        Seq<PetType> petTypes = people
                .filter(IS_PET_PERSON)
                .flatMap(person -> person.getPetTypes().keySet())
                .distinct();

        Assert.assertEquals(
                Vector.of(CAT, DOG, SNAKE, BIRD, TURTLE, HAMSTER),
                petTypes);
    }

    @Test
    public void howManyPersonHaveCats() {
        // count the number of persons who owns cats
        int count = people.count(IS_PERSON_WITH_CAT);
        Assert.assertEquals(2, count);
    }

    @Test
    public void whoOwnsTheYoungestPet() {
        // find the person who owns the youngest pet
        Option<Person> personWithYoungestPet = people
                .filter(IS_PET_PERSON)
                .minBy(Comparator.comparing(person -> person
                                .getPets()
                                .minBy(Pet::getAge)
                                .map(Pet::getAge)
                                .getOrElse(0)));
        Assert.assertEquals("Jake", personWithYoungestPet.get().getFirstName());
    }

    @Test
    public void whoOwnsTheOldestPet() {
        // find the person who owns the oldest pet
        Option<Person> personWithOldestPet = people
                .maxBy(Comparator.comparing(person -> person
                        .getPets()
                        .maxBy(Pet::getAge)
                        .map(Pet::getAge)
                        .getOrElse(50)));
        Assert.assertEquals("John", personWithOldestPet.get().getFirstName());
    }

    @Test
    public void averagePetAge() {
        // replace null by the average pet age
        Option<Double> average = people
                .flatMap(Person::getPets)
                .map(Pet::getAge)
                .average();
        Assert.assertEquals("1.89", new BigDecimal(average.get())
                .setScale(2, RoundingMode.HALF_EVEN).toPlainString());
    }

    @Test
    public void totalPetAge() {
        // replace 0 by the total age of all the pets
        Number average =  people
                .flatMap(Person::getPets)
                .map(Pet::getAge)
                .sum();
        Assert.assertEquals(17L, average);
    }
    //endregion

    @Test
    public void petsNameSorted() {
        // sort pet names alphabetically
        String sortedPetNames = people
                .flatMap(Person::getPets)
                .map(Pet::getName)
                .sorted().mkString(",");
        Assert.assertEquals("Dolly,Fuzzy,Serpy,Speedy,Spike,Spot,Tabby,Tweety,Wuzzy", sortedPetNames);
    }
    //endregion


    @Test
    public void sortByAge() {
        // Create a Seq<Integer> with ascending ordered age values.
        Seq<Integer> sortedAgeList = people
                .flatMap(Person::getPets)
                .map(Pet::getAge)
                .distinct()
                .sorted();

        Assert.assertThat(sortedAgeList.size(), CoreMatchers.equalTo(4));
        Assert.assertEquals(Vector.of(1, 2, 3, 4), sortedAgeList);
    }

    @Test
    public void sortByDescAge() {
        // Create a Seq<Integer> with descending ordered age values.
        Seq<Integer> sortedAgeList = people
                .flatMap(Person::getPets)
                .map(Pet::getAge)
                .distinct()
                .sorted()
                .reverse();

        Assert.assertThat(sortedAgeList.size(), CoreMatchers.equalTo(4));
        Assert.assertEquals(Vector.of(4, 3, 2, 1), sortedAgeList);
    }

    @Test
    public void top3OlderPets() {
        // get the names of the 3 older pets
        Seq<Pet> top3OlderPets = people
                .flatMap(Person::getPets)
                .sorted(Comparator.comparing(Pet::getAge)
                        .reversed())
                .take(3);

        Assert.assertThat(top3OlderPets.size(), CoreMatchers.equalTo(3));
        Assert.assertEquals(Vector.of("Spike", "Dolly", "Tabby"), top3OlderPets.map(Pet::getName));
    }

    @Test
    public void getFirstPersonWithAtLeast2Pets() {
        // Find the first person who owns at least 2 pets
        Option<Person> firstPersonWithAtLeast2Pets = people
                .filter(Person::isPetPerson)
                .find(person -> person.getPets().size() == 2);

        Assert.assertTrue(firstPersonWithAtLeast2Pets.isDefined());
        Assert.assertEquals("Bob", firstPersonWithAtLeast2Pets.get().getFirstName());
    }

    @Test
    public void isThereAnyPetOlderThan4() {
        // Check whether any pets older than 4 exists or not
        boolean isThereAnyPetOlderThan4 = people
                .flatMap(Person::getPets)
                .map(Pet::getAge)
                .count(age -> age > 4) > 0;

        Assert.assertFalse(isThereAnyPetOlderThan4);
    }

    @Test
    public void isEveryPetsOlderThan1() {
        // Check whether all pets are older than 1 or not
        boolean allOlderThan1 = people
                .flatMap(Person::getPets)
                .map(Pet::getAge)
                .forAll(age -> age >= 1);

        Assert.assertTrue(allOlderThan1);
    }

    private Seq<String> filterParksFor(Seq<PetType> petTypes) {
        return this.parks.filter(park -> park.getAuthorizedPetTypes().containsAll(petTypes)).map(Park::getName);
    }

    @Test
    public void getListOfPossibleParksForAWalkPerPerson() {
        // For each person described as "firstName lastName" returns the list of names possible parks to go for a walk
        Map<String, Seq<String>> possibleParksForAWalkPerPerson =
                people.toMap(
                        person -> person.getFirstName() + " " + person.getLastName(),
                        person1 -> filterParksFor(person1
                                .getPets()
                                .map(Pet::getType)
                                .distinct()));

        Assert.assertEquals(Vector.of("Jurassic", "Central", "Hippy"), possibleParksForAWalkPerPerson.get("John Doe").get());
        Assert.assertEquals(Vector.of("Jurassic", "Hippy"), possibleParksForAWalkPerPerson.get("Jake Snake").get());
    }
}