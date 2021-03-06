package com.fastcode.dvdrental.restcontrollers.core;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fastcode.dvdrental.application.core.customer.CustomerAppService;
import com.fastcode.dvdrental.application.core.payment.PaymentAppService;
import com.fastcode.dvdrental.application.core.payment.dto.*;
import com.fastcode.dvdrental.application.core.rental.RentalAppService;
import com.fastcode.dvdrental.application.core.staff.StaffAppService;
import com.fastcode.dvdrental.commons.logging.LoggingHelper;
import com.fastcode.dvdrental.commons.search.SearchUtils;
import com.fastcode.dvdrental.domain.core.address.AddressEntity;
import com.fastcode.dvdrental.domain.core.address.IAddressRepository;
import com.fastcode.dvdrental.domain.core.city.CityEntity;
import com.fastcode.dvdrental.domain.core.city.ICityRepository;
import com.fastcode.dvdrental.domain.core.country.CountryEntity;
import com.fastcode.dvdrental.domain.core.country.ICountryRepository;
import com.fastcode.dvdrental.domain.core.customer.CustomerEntity;
import com.fastcode.dvdrental.domain.core.customer.ICustomerRepository;
import com.fastcode.dvdrental.domain.core.film.FilmEntity;
import com.fastcode.dvdrental.domain.core.film.IFilmRepository;
import com.fastcode.dvdrental.domain.core.inventory.IInventoryRepository;
import com.fastcode.dvdrental.domain.core.inventory.InventoryEntity;
import com.fastcode.dvdrental.domain.core.language.ILanguageRepository;
import com.fastcode.dvdrental.domain.core.language.LanguageEntity;
import com.fastcode.dvdrental.domain.core.payment.IPaymentRepository;
import com.fastcode.dvdrental.domain.core.payment.PaymentEntity;
import com.fastcode.dvdrental.domain.core.rental.IRentalRepository;
import com.fastcode.dvdrental.domain.core.rental.RentalEntity;
import com.fastcode.dvdrental.domain.core.staff.IStaffRepository;
import com.fastcode.dvdrental.domain.core.staff.StaffEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.env.Environment;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
public class PaymentControllerTest {

    @Autowired
    protected SortHandlerMethodArgumentResolver sortArgumentResolver;

    @Autowired
    @Qualifier("paymentRepository")
    protected IPaymentRepository payment_repository;

    @Autowired
    @Qualifier("customerRepository")
    protected ICustomerRepository customerRepository;

    @Autowired
    @Qualifier("rentalRepository")
    protected IRentalRepository rentalRepository;

    @Autowired
    @Qualifier("staffRepository")
    protected IStaffRepository staffRepository;

    @Autowired
    @Qualifier("languageRepository")
    protected ILanguageRepository languageRepository;

    @Autowired
    @Qualifier("addressRepository")
    protected IAddressRepository addressRepository;

    @Autowired
    @Qualifier("filmRepository")
    protected IFilmRepository filmRepository;

    @Autowired
    @Qualifier("countryRepository")
    protected ICountryRepository countryRepository;

    @Autowired
    @Qualifier("cityRepository")
    protected ICityRepository cityRepository;

    @Autowired
    @Qualifier("inventoryRepository")
    protected IInventoryRepository inventoryRepository;

    @SpyBean
    @Qualifier("paymentAppService")
    protected PaymentAppService paymentAppService;

    @SpyBean
    @Qualifier("customerAppService")
    protected CustomerAppService customerAppService;

    @SpyBean
    @Qualifier("rentalAppService")
    protected RentalAppService rentalAppService;

    @SpyBean
    @Qualifier("staffAppService")
    protected StaffAppService staffAppService;

    @SpyBean
    protected LoggingHelper logHelper;

    @SpyBean
    protected Environment env;

    @Mock
    protected Logger loggerMock;

    protected PaymentEntity payment;

    protected MockMvc mvc;

    @Autowired
    EntityManagerFactory emf;

    static EntityManagerFactory emfs;

    static int relationCount = 10;

    int countRental = 10;

    int countLanguage = 10;

    int countAddress = 10;

    int countFilm = 10;

    int countCustomer = 10;

    int countStaff = 10;

    int countCountry = 10;

    int countCity = 10;

    int countInventory = 10;

    @PostConstruct
    public void init() {
        emfs = emf;
    }

    @AfterClass
    public static void cleanup() {
        EntityManager em = emfs.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        em.createNativeQuery("truncate table PAYMENT RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table CUSTOMER RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table RENTAL RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table STAFF RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table LANGUAGE RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table ADDRESS RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table FILM RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table COUNTRY RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table CITY RESTART IDENTITY").executeUpdate();

        em.createNativeQuery("truncate table INVENTORY RESTART IDENTITY").executeUpdate();
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
        em.getTransaction().commit();
    }

    public RentalEntity createRentalEntity() {
        if (countRental > 60) {
            countRental = 10;
        }

        RentalEntity rentalEntity = new RentalEntity();
        rentalEntity.setRentalDate(SearchUtils.stringToLocalDate("19" + countRental + "-09-01"));
        rentalEntity.setRentalId(relationCount);
        rentalEntity.setReturnDate(SearchUtils.stringToLocalDate("19" + countRental + "-09-01"));
        rentalEntity.setVersiono(0L);
        relationCount++;
        InventoryEntity inventory = createInventoryEntity();
        rentalEntity.setInventory(inventory);
        StaffEntity staff = createStaffEntity();
        rentalEntity.setStaff(staff);
        CustomerEntity customer = createCustomerEntity();
        rentalEntity.setCustomer(customer);
        if (!rentalRepository.findAll().contains(rentalEntity)) {
            rentalEntity = rentalRepository.save(rentalEntity);
        }
        countRental++;
        return rentalEntity;
    }

    public LanguageEntity createLanguageEntity() {
        if (countLanguage > 60) {
            countLanguage = 10;
        }

        LanguageEntity languageEntity = new LanguageEntity();
        languageEntity.setLanguageId(relationCount);
        languageEntity.setName(String.valueOf(relationCount));
        languageEntity.setVersiono(0L);
        relationCount++;
        if (!languageRepository.findAll().contains(languageEntity)) {
            languageEntity = languageRepository.save(languageEntity);
        }
        countLanguage++;
        return languageEntity;
    }

    public AddressEntity createAddressEntity() {
        if (countAddress > 60) {
            countAddress = 10;
        }

        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setAddress(String.valueOf(relationCount));
        addressEntity.setAddress2(String.valueOf(relationCount));
        addressEntity.setAddressId(relationCount);
        addressEntity.setDistrict(String.valueOf(relationCount));
        addressEntity.setPhone(String.valueOf(relationCount));
        addressEntity.setPostalCode(String.valueOf(relationCount));
        addressEntity.setVersiono(0L);
        relationCount++;
        CityEntity city = createCityEntity();
        addressEntity.setCity(city);
        if (!addressRepository.findAll().contains(addressEntity)) {
            addressEntity = addressRepository.save(addressEntity);
        }
        countAddress++;
        return addressEntity;
    }

    public FilmEntity createFilmEntity() {
        if (countFilm > 60) {
            countFilm = 10;
        }

        FilmEntity filmEntity = new FilmEntity();
        filmEntity.setDescription(String.valueOf(relationCount));
        filmEntity.setFilmId(relationCount);
        filmEntity.setLength((short) relationCount);
        filmEntity.setRating(String.valueOf(relationCount));
        filmEntity.setReleaseYear(relationCount);
        filmEntity.setRentalDuration((short) relationCount);
        filmEntity.setRentalRate(BigDecimal.valueOf(relationCount));
        filmEntity.setReplacementCost(BigDecimal.valueOf(relationCount));
        filmEntity.setTitle(String.valueOf(relationCount));
        filmEntity.setVersiono(0L);
        relationCount++;
        LanguageEntity language = createLanguageEntity();
        filmEntity.setLanguage(language);
        if (!filmRepository.findAll().contains(filmEntity)) {
            filmEntity = filmRepository.save(filmEntity);
        }
        countFilm++;
        return filmEntity;
    }

    public CustomerEntity createCustomerEntity() {
        if (countCustomer > 60) {
            countCustomer = 10;
        }

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setActive(relationCount);
        customerEntity.setActivebool(false);
        customerEntity.setCustomerId(relationCount);
        customerEntity.setEmail(String.valueOf(relationCount));
        customerEntity.setFirstName(String.valueOf(relationCount));
        customerEntity.setLastName(String.valueOf(relationCount));
        customerEntity.setStoreId((short) relationCount);
        customerEntity.setVersiono(0L);
        relationCount++;
        AddressEntity address = createAddressEntity();
        customerEntity.setAddress(address);
        if (!customerRepository.findAll().contains(customerEntity)) {
            customerEntity = customerRepository.save(customerEntity);
        }
        countCustomer++;
        return customerEntity;
    }

    public StaffEntity createStaffEntity() {
        if (countStaff > 60) {
            countStaff = 10;
        }

        StaffEntity staffEntity = new StaffEntity();
        staffEntity.setActive(false);
        staffEntity.setEmail(String.valueOf(relationCount));
        staffEntity.setFirstName(String.valueOf(relationCount));
        staffEntity.setLastName(String.valueOf(relationCount));
        staffEntity.setPassword(String.valueOf(relationCount));
        staffEntity.setStaffId(relationCount);
        staffEntity.setStoreId((short) relationCount);
        staffEntity.setUsername(String.valueOf(relationCount));
        staffEntity.setVersiono(0L);
        relationCount++;
        AddressEntity address = createAddressEntity();
        staffEntity.setAddress(address);
        if (!staffRepository.findAll().contains(staffEntity)) {
            staffEntity = staffRepository.save(staffEntity);
        }
        countStaff++;
        return staffEntity;
    }

    public CountryEntity createCountryEntity() {
        if (countCountry > 60) {
            countCountry = 10;
        }

        CountryEntity countryEntity = new CountryEntity();
        countryEntity.setCountry(String.valueOf(relationCount));
        countryEntity.setCountryId(relationCount);
        countryEntity.setVersiono(0L);
        relationCount++;
        if (!countryRepository.findAll().contains(countryEntity)) {
            countryEntity = countryRepository.save(countryEntity);
        }
        countCountry++;
        return countryEntity;
    }

    public CityEntity createCityEntity() {
        if (countCity > 60) {
            countCity = 10;
        }

        CityEntity cityEntity = new CityEntity();
        cityEntity.setCity(String.valueOf(relationCount));
        cityEntity.setCityId(relationCount);
        cityEntity.setVersiono(0L);
        relationCount++;
        CountryEntity country = createCountryEntity();
        cityEntity.setCountry(country);
        if (!cityRepository.findAll().contains(cityEntity)) {
            cityEntity = cityRepository.save(cityEntity);
        }
        countCity++;
        return cityEntity;
    }

    public InventoryEntity createInventoryEntity() {
        if (countInventory > 60) {
            countInventory = 10;
        }

        InventoryEntity inventoryEntity = new InventoryEntity();
        inventoryEntity.setInventoryId(relationCount);
        inventoryEntity.setStoreId((short) relationCount);
        inventoryEntity.setVersiono(0L);
        relationCount++;
        FilmEntity film = createFilmEntity();
        inventoryEntity.setFilm(film);
        if (!inventoryRepository.findAll().contains(inventoryEntity)) {
            inventoryEntity = inventoryRepository.save(inventoryEntity);
        }
        countInventory++;
        return inventoryEntity;
    }

    public PaymentEntity createEntity() {
        CustomerEntity customer = createCustomerEntity();
        RentalEntity rental = createRentalEntity();
        StaffEntity staff = createStaffEntity();

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setAmount(new BigDecimal("1"));
        paymentEntity.setPaymentDate(SearchUtils.stringToLocalDate("1996-09-01"));
        paymentEntity.setPaymentId(1);
        paymentEntity.setVersiono(0L);
        paymentEntity.setCustomer(customer);
        paymentEntity.setRental(rental);
        paymentEntity.setStaff(staff);

        return paymentEntity;
    }

    public CreatePaymentInput createPaymentInput() {
        CreatePaymentInput paymentInput = new CreatePaymentInput();
        paymentInput.setAmount(new BigDecimal("5"));
        paymentInput.setPaymentDate(SearchUtils.stringToLocalDate("1996-08-10"));

        return paymentInput;
    }

    public PaymentEntity createNewEntity() {
        PaymentEntity payment = new PaymentEntity();
        payment.setAmount(new BigDecimal("3"));
        payment.setPaymentDate(SearchUtils.stringToLocalDate("1996-08-11"));
        payment.setPaymentId(3);

        return payment;
    }

    public PaymentEntity createUpdateEntity() {
        PaymentEntity payment = new PaymentEntity();
        payment.setAmount(new BigDecimal("3"));
        payment.setPaymentDate(SearchUtils.stringToLocalDate("1996-09-09"));
        payment.setPaymentId(4);

        return payment;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        final PaymentController paymentController = new PaymentController(
            paymentAppService,
            customerAppService,
            rentalAppService,
            staffAppService,
            logHelper,
            env
        );
        when(logHelper.getLogger()).thenReturn(loggerMock);
        doNothing().when(loggerMock).error(anyString());

        this.mvc =
            MockMvcBuilders
                .standaloneSetup(paymentController)
                .setCustomArgumentResolvers(sortArgumentResolver)
                .setControllerAdvice()
                .build();
    }

    @Before
    public void initTest() {
        payment = createEntity();
        List<PaymentEntity> list = payment_repository.findAll();
        if (!list.contains(payment)) {
            payment = payment_repository.save(payment);
        }
    }

    @Test
    public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
        mvc
            .perform(get("/payment/" + payment.getPaymentId() + "/").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void FindById_IdIsNotValid_ReturnStatusNotFound() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc.perform(get("/payment/999").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Not found"));
    }

    @Test
    public void CreatePayment_PaymentDoesNotExist_ReturnStatusOk() throws Exception {
        CreatePaymentInput paymentInput = createPaymentInput();

        CustomerEntity customer = createCustomerEntity();

        paymentInput.setCustomerId(Integer.parseInt(customer.getCustomerId().toString()));

        RentalEntity rental = createRentalEntity();

        paymentInput.setRentalId(Integer.parseInt(rental.getRentalId().toString()));

        StaffEntity staff = createStaffEntity();

        paymentInput.setStaffId(Integer.parseInt(staff.getStaffId().toString()));

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();

        String json = ow.writeValueAsString(paymentInput);

        mvc.perform(post("/payment").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
    }

    @Test
    public void CreatePayment_customerDoesNotExists_ThrowEntityNotFoundException() throws Exception {
        CreatePaymentInput payment = createPaymentInput();
        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();

        String json = ow.writeValueAsString(payment);

        org.assertj.core.api.Assertions.assertThatThrownBy(
            () ->
                mvc
                    .perform(post("/payment").contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isNotFound())
        );
    }

    @Test
    public void CreatePayment_rentalDoesNotExists_ThrowEntityNotFoundException() throws Exception {
        CreatePaymentInput payment = createPaymentInput();
        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();

        String json = ow.writeValueAsString(payment);

        org.assertj.core.api.Assertions.assertThatThrownBy(
            () ->
                mvc
                    .perform(post("/payment").contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isNotFound())
        );
    }

    @Test
    public void CreatePayment_staffDoesNotExists_ThrowEntityNotFoundException() throws Exception {
        CreatePaymentInput payment = createPaymentInput();
        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();

        String json = ow.writeValueAsString(payment);

        org.assertj.core.api.Assertions.assertThatThrownBy(
            () ->
                mvc
                    .perform(post("/payment").contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isNotFound())
        );
    }

    @Test
    public void DeletePayment_IdIsNotValid_ThrowEntityNotFoundException() {
        doReturn(null).when(paymentAppService).findById(999);
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(delete("/payment/999").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("There does not exist a payment with a id=999"));
    }

    @Test
    public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
        PaymentEntity entity = createNewEntity();
        entity.setVersiono(0L);
        CustomerEntity customer = createCustomerEntity();
        entity.setCustomer(customer);
        RentalEntity rental = createRentalEntity();
        entity.setRental(rental);
        StaffEntity staff = createStaffEntity();
        entity.setStaff(staff);
        entity = payment_repository.save(entity);

        FindPaymentByIdOutput output = new FindPaymentByIdOutput();
        output.setAmount(entity.getAmount());
        output.setPaymentId(entity.getPaymentId());

        Mockito.doReturn(output).when(paymentAppService).findById(entity.getPaymentId());

        //    Mockito.when(paymentAppService.findById(entity.getPaymentId())).thenReturn(output);

        mvc
            .perform(delete("/payment/" + entity.getPaymentId() + "/").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void UpdatePayment_PaymentDoesNotExist_ReturnStatusNotFound() throws Exception {
        doReturn(null).when(paymentAppService).findById(999);

        UpdatePaymentInput payment = new UpdatePaymentInput();
        payment.setAmount(new BigDecimal("999"));
        payment.setPaymentDate(SearchUtils.stringToLocalDate("1996-09-28"));
        payment.setPaymentId(999);

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(payment);

        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(put("/payment/999").contentType(MediaType.APPLICATION_JSON).content(json))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Unable to update. Payment with id=999 not found."));
    }

    @Test
    public void UpdatePayment_PaymentExists_ReturnStatusOk() throws Exception {
        PaymentEntity entity = createUpdateEntity();
        entity.setVersiono(0L);

        CustomerEntity customer = createCustomerEntity();
        entity.setCustomer(customer);
        RentalEntity rental = createRentalEntity();
        entity.setRental(rental);
        StaffEntity staff = createStaffEntity();
        entity.setStaff(staff);
        entity = payment_repository.save(entity);
        FindPaymentByIdOutput output = new FindPaymentByIdOutput();
        output.setAmount(entity.getAmount());
        output.setPaymentDate(entity.getPaymentDate());
        output.setPaymentId(entity.getPaymentId());
        output.setVersiono(entity.getVersiono());

        Mockito.when(paymentAppService.findById(entity.getPaymentId())).thenReturn(output);

        UpdatePaymentInput paymentInput = new UpdatePaymentInput();
        paymentInput.setAmount(entity.getAmount());
        paymentInput.setPaymentId(entity.getPaymentId());

        paymentInput.setCustomerId(Integer.parseInt(customer.getCustomerId().toString()));
        paymentInput.setRentalId(Integer.parseInt(rental.getRentalId().toString()));
        paymentInput.setStaffId(Integer.parseInt(staff.getStaffId().toString()));

        ObjectWriter ow = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .writer()
            .withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(paymentInput);

        mvc
            .perform(
                put("/payment/" + entity.getPaymentId() + "/").contentType(MediaType.APPLICATION_JSON).content(json)
            )
            .andExpect(status().isOk());

        PaymentEntity de = createUpdateEntity();
        de.setPaymentId(entity.getPaymentId());
        payment_repository.delete(de);
    }

    @Test
    public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {
        mvc
            .perform(
                get("/payment?search=paymentId[equals]=1&limit=10&offset=1").contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());
    }

    @Test
    public void FindAll_SearchIsNotNullAndPropertyIsNotValid_ThrowException() throws Exception {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(
                            get("/payment?search=paymentpaymentId[equals]=1&limit=10&offset=1")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
            )
            .hasCause(new Exception("Wrong URL Format: Property paymentpaymentId not found!"));
    }

    @Test
    public void GetCustomer_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(get("/payment/999/customer").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Not found"));
    }

    @Test
    public void GetCustomer_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
        mvc
            .perform(get("/payment/" + payment.getPaymentId() + "/customer").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void GetRental_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(get("/payment/999/rental").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Not found"));
    }

    @Test
    public void GetRental_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
        mvc
            .perform(get("/payment/" + payment.getPaymentId() + "/rental").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void GetStaff_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(
                () ->
                    mvc
                        .perform(get("/payment/999/staff").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
            )
            .hasCause(new EntityNotFoundException("Not found"));
    }

    @Test
    public void GetStaff_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
        mvc
            .perform(get("/payment/" + payment.getPaymentId() + "/staff").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
