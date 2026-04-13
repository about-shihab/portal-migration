package com.iict.buet.customer_portal.service.impl;

import com.iict.buet.customer_portal.dto.Response;
import com.iict.buet.customer_portal.dto.RoleDto;
import com.iict.buet.customer_portal.model.Role;
import com.iict.buet.customer_portal.repository.RoleRepository;
import com.iict.buet.customer_portal.repository.UserRepository;
import com.iict.buet.customer_portal.service.RoleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service("roleService")
public class RoleServiceImpl implements RoleService {
    private static final Logger logger = LogManager.getLogger(RoleServiceImpl.class.getName());
    private final UserRepository userRepository;
    private final String root = "Role";
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public RoleServiceImpl(UserRepository userRepository, ModelMapper modelMapper, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
    }

    @Override
    public Response create(RoleDto roleDto) {
        Role role = modelMapper.map(roleDto, Role.class);
        return null;
    }

    @Override
    public Response update(Long id, RoleDto roleDto) {
        /*Response notFoundFailureResponse = utilityService.getNullResponse(roleRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Role role = utilityService.getById(roleRepository, id);
            return utilityService.getUpdateResponse(role, roleDto, roleRepository);
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }*/
        return null;
    }

    @Override
    public Response delete(Long id) {
        /*Response notFoundFailureResponse = utilityService.getNullResponse(roleRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Role role = utilityService.getById(roleRepository, id);
            return utilityService.deleteEntityResponse(role, roleRepository);
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }*/
        return null;
    }

    @Override
    public Response get(Long id) {
        /*Response notFoundFailureResponse = utilityService.getNullResponse(roleRepository, id);
        if (notFoundFailureResponse != null) {
            return notFoundFailureResponse;
        }
        try {
            Role role = utilityService.getById(roleRepository, id);
            RoleDto roleDto = modelMapper.map(role, RoleDto.class);
            return utilityService.getGetResponse(roleDto, root);
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }*/
        return null;
    }

    @Override
    public Response getAll(Pageable pageable, boolean isExport, String search, String status) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> criteriaQuery = criteriaBuilder.createQuery(Role.class);
        Root<Role> rootEntity = criteriaQuery.from(Role.class);

        addPredicates(criteriaBuilder, criteriaQuery, rootEntity, search);

        TypedQuery<Role> typedQuery = entityManager.createQuery(criteriaQuery);
        return getAllResponse(criteriaQuery, typedQuery, pageable, isExport);
    }


    private Response getAllResponse(CriteriaQuery<Role> criteriaQuery, TypedQuery<Role> typedQuery, Pageable pageable, boolean isExport) {
        /*if(utilityService.getAllFailureResponse(typedQuery, isExport, pageable, root) != null){
            return utilityService.getAllFailureResponse(typedQuery, isExport, pageable, root);
        }
        long totalRows = this.getTotalRows(criteriaQuery);
        Page<Role> roles = utilityService.getAllPage(typedQuery, pageable);
        return utilityService.getAllSuccessResponse(totalRows, this.getResponseDtoList(roles), root);*/
        return null;
    }

    private long getTotalRows(CriteriaQuery<Role> criteriaQuery) {
        TypedQuery<Role> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList().size();
    }

    private void addPredicates(CriteriaBuilder criteriaBuilder, CriteriaQuery<Role> criteriaQuery, Root<Role> rootEntity, String search) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.and(criteriaBuilder.isTrue(rootEntity.<Boolean>get("active")), criteriaBuilder.equal(rootEntity.<String>get("status"), "ACTIVE")));

        if (search != null && search.trim().length() > 0) {

            Predicate pLike = criteriaBuilder.or(
                    criteriaBuilder.like(rootEntity.<String>get("name"), "%" + search + "%"));
            predicates.add(pLike);
        }

        if (predicates.isEmpty()) {
            logger.error("predicates isEmpty ");
            criteriaQuery.select(rootEntity);
        } else {
            logger.error("predicates is not Empty ");
            criteriaQuery.select(rootEntity).where(predicates.toArray(new Predicate[predicates.size()]));
        }
    }


    private List<RoleDto> getResponseDtoList(Page<Role> roles) {
        List<RoleDto> responseDtos = new ArrayList<>();
        roles.forEach(role  -> {
            RoleDto roleDto = modelMapper.map(role, RoleDto.class);
            responseDtos.add(roleDto);
        });
        return responseDtos;
    }

}
