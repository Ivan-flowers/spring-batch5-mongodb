package com.bootlabs.springbatch5mongodb.job.step;

import com.bootlabs.springbatch5mongodb.domain.model.TripCsvLine;
import com.bootlabs.springbatch5mongodb.domain.document.Trips;
import com.bootlabs.springbatch5mongodb.domain.enums.UserGender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;

@Slf4j
@Component
public class TripItemProcessor implements ItemProcessor<Trips, TripCsvLine> {


	@Override
	public TripCsvLine process(Trips item) {
		//LOGGER.info("Trips processor {}", item.toString());

		var age = LocalDate.now().getYear() - item.getBirthYear();
		var gender = UserGender.getType(item.getGender()).name();
		Duration duration = Duration.ofSeconds(item.getDuration());
		String formattedDuration= String.format("%02d:%02d:%02d", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());

		return new TripCsvLine(item.getBikeId(), age, gender, formattedDuration, item.getStartStationName(), item.getEndStationName());
	}
}
