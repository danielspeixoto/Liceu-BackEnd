package com.liceu.server

import com.liceu.server.data.TestMongoTagRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runners.Suite
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Suite.SuiteClasses(TestMongoTagRepository::class)
class ServerApplicationTests {

	@Test
	fun contextLoads() {
	}

}
