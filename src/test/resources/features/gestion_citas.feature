Feature: Gestion de citas en el taller mecanico

  Scenario: Registro exitoso de MANTENIMIENTO_LIGERO con otro mecanico
    Given un mecanico 2 libre el DIA a las "10:00"
    When se intenta registrar un MANTENIMIENTO_LIGERO para la PLACA con el mecanico 2 a las "10:00"
    Then la cita queda en estado PROGRAMADA y se notifica la confirmacion

  Scenario: Intento de registro con mecanico ocupado iniciando a las 11:00
    Given un mecanico 1 ocupado el DIA de "10:00" a "12:00"
    When se intenta registrar un MANTENIMIENTO_LIGERO para la PLACA con el mecanico 1 a las "11:00"
    Then el registro es rechazado por conflicto de horario

  Scenario: Intento de registro con mecanico ocupado iniciando a las 12:00
    Given un mecanico 1 ocupado el DIA de "10:00" a "12:00"
    When se intenta registrar un MANTENIMIENTO_LIGERO para la PLACA con el mecanico 1 a las "12:00"
    Then el registro es exitoso por finalizar la cita previa