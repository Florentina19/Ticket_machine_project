import java.time.LocalDate
// Assumes other model/core/user files are in the same src folder (no package declarations)

fun main() {
    val machine = TicketMachine("Central")

    // Hard-code sample stations (required by brief)
    machine.addStation(Station("London", 12.50, 20.00))
    machine.addStation(Station("Bristol", 8.00, 14.00))
    machine.addStation(Station("Oxford", 6.50, 11.00))

    // Hard-coded users (group C)
    val adminUser = Admin("admin", "adminpass")
    val normalUser = User("guest", "guestpass", false)
    val users = listOf(adminUser, normalUser)

    val loginManager = LoginManager(users)

    mainMenu(machine, loginManager)
}

fun mainMenu(machine: TicketMachine, loginManager: LoginManager) {
    while (true) {
        println("\n====== Ticket Machine ======")
        println("1) User Mode")
        println("2) Admin Login")
        println("3) Exit")
        print("Choose: ")
        when (readLine()?.trim()) {
            "1" -> userMenu(machine)
            "2" -> {
                val user = loginManager.login()
                if (user == null) {
                    println("Login failed.")
                } else if (!user.isAdmin) {
                    println("Access denied. Not an admin.")
                } else {
                    adminMenuWithLogin(machine, loginManager, user as Admin)
                }
            }
            "3" -> {
                println("Goodbye.")
                return
            }
            else -> println("Invalid choice.")
        }
    }
}

fun userMenu(machine: TicketMachine) {
    while (true) {
        println("\n--- User Menu ---")
        println("1) Search ticket")
        println("2) Insert money")
        println("3) Buy ticket")
        println("4) Refund")
        println("5) Back")
        print("Choose: ")
        when (readLine()?.trim()) {
            "1" -> {
                print("Enter destination: ")
                val dest = readLine()?.trim() ?: ""
                val st = machine.searchStation(dest)
                if (st == null) println("Destination not found.")
                else {
                    println("${st.name} | Single: £${String.format("%.2f", st.singlePrice)} | Return: £${String.format("%.2f", st.returnPrice)}")
                }
            }
            "2" -> {
                print("Enter amount to insert: ")
                val amt = readLine()?.toDoubleOrNull()
                if (amt == null) println("Invalid amount.")
                else machine.insertMoney(amt)
            }
            "3" -> {
                print("Enter destination: ")
                val dest = readLine()?.trim() ?: ""
                print("Select ticket type (1=Single, 2=Return): ")
                val t = when (readLine()?.trim()) {
                    "1" -> TicketType.SINGLE
                    "2" -> TicketType.RETURN
                    else -> {
                        println("Invalid type."); null
                    }
                }
                if (t != null) {
                    val success = machine.buyTicket(dest, t)
                    if (!success) {
                        println("Buy failed.")
                    }
                }
            }
            "4" -> machine.refund()
            "5" -> return
            else -> println("Invalid choice.")
        }
    }
}

fun adminMenuWithLogin(machine: TicketMachine, loginManager: LoginManager, admin: Admin) {
    while (true) {
        println("\n--- Admin Menu (Logged in as ${admin.username}) ---")
        println("1) View stations")
        println("2) Add station")
        println("3) Edit station")
        println("4) Change all prices by factor")
        println("5) View takings & inserted credit (debug)")
        println("6) Offer management")
        println("7) Logout")
        print("Choose: ")
        when (readLine()?.trim()) {
            "1" -> machine.viewAllStations()
            "2" -> machine.addStationInteractive()
            "3" -> {
                print("Enter station name to edit: ")
                val name = readLine()?.trim() ?: ""
                print("Enter new single price (blank to skip): ")
                val singleStr = readLine()?.trim()
                val single = singleStr?.toDoubleOrNull()
                print("Enter new return price (blank to skip): ")
                val retStr = readLine()?.trim()
                val ret = retStr?.toDoubleOrNull()
                machine.editStation(name, single, ret)
            }
            "4" -> {
                print("Enter factor (e.g., 1.1 for +10%, 0.9 for -10%): ")
                val factor = readLine()?.toDoubleOrNull()
                if (factor == null) println("Invalid factor.") else machine.changeAllPrices(factor)
            }
            "5" -> {
                println("Total takings: £${String.format("%.2f", machine.getTotalTakings())}")
                println("Inserted credit: £${String.format("%.2f", machine.getInsertedAmount())}")
            }
            "6" -> offerManagementMenu(machine)
            "7" -> {
                println("Logging out.")
                return
            }
            else -> println("Invalid choice.")
        }
    }
}

fun offerManagementMenu(machine: TicketMachine) {
    while (true) {
        println("\n--- Offer Management ---")
        println("1) View offers")
        println("2) Add offer")
        println("3) Delete offers for station")
        println("4) Back")
        print("Choose: ")
        when (readLine()?.trim()) {
            "1" -> machine.viewOffers()
            "2" -> {
                print("Enter station name for offer: ")
                val stationName = readLine()?.trim() ?: ""
                print("Enter start date (YYYY-MM-DD): ")
                val start = readLine()?.trim()
                print("Enter end date (YYYY-MM-DD): ")
                val end = readLine()?.trim()
                print("Enter discount factor (e.g., 0.8 for 20% off): ")
                val factor = readLine()?.toDoubleOrNull()
                if (start == null || end == null || factor == null) {
                    println("Invalid input.")
                } else {
                    try {
                        val sDate = LocalDate.parse(start)
                        val eDate = LocalDate.parse(end)
                        if (eDate.isBefore(sDate)) {
                            println("End date must be on or after start date.")
                        } else {
                            machine.addOffer(SpecialOffer(stationName, sDate, eDate, factor))
                        }
                    } catch (ex: Exception) {
                        println("Date parse error: ${ex.message}")
                    }
                }
            }
            "3" -> {
                print("Enter station name to delete offers for: ")
                val st = readLine()?.trim() ?: ""
                machine.deleteOffersForStation(st)
            }
            "4" -> return
            else -> println("Invalid choice.")
        }
    }
}
