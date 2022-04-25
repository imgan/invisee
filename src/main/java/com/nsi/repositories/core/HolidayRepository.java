package com.nsi.repositories.core;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nsi.domain.core.Holiday;
import org.springframework.data.repository.query.Param;

public interface HolidayRepository extends JpaRepository<Holiday, Long>{

    @Query("select hd from Holiday hd where DATE(hd.holidayDate) = DATE(?1)")
    public List<Holiday> find(Date currentDate);
	
	@Query("select h.holidayDate from Holiday h ")
	public List<Date> getHolidayDate();

	@Query(value = "FROM Holiday h WHERE to_char(h.holidayDate,'yyyy-MM-dd')>=:date")
	public List<Holiday> findAllByHolidayDateWithCustomQuery(@Param("date") String holidayDate);
}
