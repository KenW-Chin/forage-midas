import org.springframework.context.annotation.KafkaProducer;

@Autowired
private KafkaProducer kafkaProducer;

class KafkaProducer{
    public void send(String message) {
        kafkaTemplate.send(topic, message);
    }
}