package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nsi.domain.core.CustomerDocument;
import com.nsi.domain.core.User;

public interface CustomerDocumentRepository extends JpaRepository<CustomerDocument, Long> {

  @Query("select c from CustomerDocument c where c.user=?1 and c.rowStatus=false and c.documentType in (select d.code from DocumentType d where d.rowStatus=true and d.type=?2) order by c.createdOn desc")
  public List<CustomerDocument> findByDocumentTypeWithQuery(User user, String type);

  public CustomerDocument findByUserAndRowStatusAndDocumentType(User user, Boolean rowStatus,
      String codes);

  @Query("select distinct cd.documentType from CustomerDocument cd where cd.user=?1 and cd.documentType in (?2,?3) ")
  public List<String> getDocValid(User user, String docTyp01, String docTyp03);

  public List<CustomerDocument> findByDocumentTypeAndUserOrderByCreatedOnDesc(String documentType,
      User user);

  public List<CustomerDocument> findTop1ByDocumentTypeAndUserAndRowStatusOrderByCreatedOnDesc(
      String documentType, User user, Boolean rowStatus);

  public CustomerDocument findByFileKey(String fileKey);

  public CustomerDocument findByFileKeyAndUser(String fileKey, User user);

  public List<CustomerDocument> findTop1ByDocumentTypeAndUserOrderByCreatedOnDesc(
      String documentType, User user);

  CustomerDocument findByRowStatusIsTrueAndDocumentTypeAndUser(String type, User user);

  List<CustomerDocument> findAllByRowStatusIsTrueAndUser(User user);

  List<CustomerDocument> findAllByRowStatusIsTrueAndUserAndDocumentTypeIn(User user,
      List<String> documentType);
}
