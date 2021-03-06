package me.hufman.androidautoidrive

import com.nhaarman.mockito_kotlin.*
import me.hufman.idriveconnectionkit.IDriveConnection
import me.hufman.idriveconnectionkit.android.CarAppResources
import me.hufman.idriveconnectionkit.android.security.SecurityAccess
import org.junit.Test
import java.io.ByteArrayInputStream

class TestCarInformationDiscovery {
	val securityAccess = mock<SecurityAccess> {
		on { signChallenge(any(), any() )} doReturn ByteArray(512)
	}
	val carAppResources = mock<CarAppResources> {
		on { getAppCertificate() } doReturn ByteArrayInputStream(ByteArray(0))
		on { getUiDescription() } doReturn ByteArrayInputStream(ByteArray(0))
		on { getImagesDB(any()) } doReturn ByteArrayInputStream(ByteArray(0))
		on { getTextsDB(any()) } doReturn ByteArrayInputStream(ByteArray(0))
	}
	val listener = mock<CarInformationDiscoveryListener>()

	@Test
	fun testDiscovery() {
		val mockServer = MockBMWRemotingServer()
		IDriveConnection.mockRemotingServer = mockServer
		val app = CarInformationDiscovery(securityAccess, carAppResources, listener)
		app.onCreate()
		verify(listener).onCapabilities(app.capabilities!!)

		// test a CDS update
		app.carappListener.cds_onPropertyChangedEvent(1, "1", "propertyName", "{\"key\": \"propertyValue\"}")
		verify(listener).onCdsProperty(eq("propertyName"), eq("{\"key\": \"propertyValue\"}"), argThat {getString("key") == "propertyValue"})
	}
}