//getAll
CREATE DEFINER=`root`@`%` PROCEDURE `example`.`get_all_employees`()
BEGIN
    SELECT * FROM employees;
END

//createEmployee
CREATE DEFINER=`root`@`%` PROCEDURE `example`.`insert_employee`(
    IN p_id INT,
    IN p_name VARCHAR(100),
    IN p_position VARCHAR(100),
    IN p_salary DECIMAL(10,2),
    IN p_hire_date DATE,
    IN p_department VARCHAR(50)
)
BEGIN
    INSERT INTO employees (id, name, position, salary, hire_date, department)
    VALUES (p_id, p_name, p_position, p_salary, p_hire_date, p_department);
END

//getEmployeeById
CREATE DEFINER=`root`@`%` PROCEDURE `example`.`get_employee_by_id`(
    IN employee_id INT
)
BEGIN
    SELECT *
    FROM employees
    WHERE id = employee_id;
END

//updateEmployeeById
CREATE DEFINER=`root`@`%` PROCEDURE `example`.`update_employee_by_id`(
    IN employee_id INT,
    IN employee_name VARCHAR(100),
    IN employee_position VARCHAR(100),
    IN employee_salary DECIMAL(10,2),
    IN employee_hire_date DATE,
    IN employee_department VARCHAR(50)
)
BEGIN
    UPDATE employees
    SET name = employee_name,
        position = employee_position,
        salary = employee_salary,
        hire_date = employee_hire_date,
        department = employee_department
    WHERE id = employee_id;
END

//deleteEmployeeByID
CREATE DEFINER=`root`@`%` PROCEDURE `example`.`delete_employee_by_id`(
    IN employee_id INT
)
BEGIN
    DELETE FROM employees
    WHERE id = employee_id;
END