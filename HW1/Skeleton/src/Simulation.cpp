#include "Simulation.h"
#include "Auxiliary.h"
#include "Action.h"
#include <fstream>
#include <iostream>
#include <string>
using namespace std;
Simulation::~Simulation()
{
    for (BaseAction *action : actionsLog) {
        delete action;
    }
    for (Settlement *settlement : settlements) {
        delete settlement;
    }
    actionsLog.clear();
    settlements.clear();
    plans.clear(); // Plans are not dynamically allocated
}
Simulation::Simulation(const Simulation &other)
    : isRunning(other.isRunning),
      planCounter(other.planCounter),
      actionsLog(), // Initialize actionsLog as empty, then copy contents
      plans(), // Initialize plans as empty, then copy contents
      settlements(), // Initialize settlements as empty, then copy contents
      facilitiesOptions(other.facilitiesOptions) // Direct copy of facility options

{
    //  Deep copy of actionsLog
    for (BaseAction *action : other.actionsLog)
    {                                          // TODO:BaseAction has a clone()
        actionsLog.push_back(action->clone()); // Assumes BaseAction has a clone() method
    }
    for (Settlement *settlement : other.settlements)
    {
        settlements.push_back(new Settlement(*settlement));
    }

    for (int i = 0; i < (int)other.facilitiesOptions.size(); i++)
    {
        this->facilitiesOptions.push_back(other.facilitiesOptions[i]);
    }

    for (const Plan &otherPlan : other.plans)
    {
        string settlName = otherPlan.getSettlement().getName();
         Plan newPlan = Plan(*getSettlement(settlName), otherPlan);
        plans.push_back(newPlan);
    }

}
Simulation &Simulation::operator=(const Simulation &other)
{
        if (this != &other)
    {
        
        for (BaseAction *action : actionsLog)
        {
            delete action;
        }
        for (int i = 0; i < (int)other.plans.size(); i++)
        {
            Settlement *mysett = getSettlement(other.plans[i].getSettlement().getName());

            if (mysett == &other.plans[i].getSettlement())
            {

                mysett = nullptr;
                mysett = new Settlement(other.plans[i].getSettlement().getName(), other.plans[i].getSettlement().getType());
            }
            
        }
        
        actionsLog.clear();

        for (Settlement *settlement : settlements)
        {
            delete settlement;
        }
        
        settlements.clear();

        isRunning = other.isRunning;
        planCounter = other.planCounter;

        for (BaseAction *action : other.actionsLog)
        {
            actionsLog.push_back(action->clone());
        }
        for (Settlement *settlement : other.settlements)
        {
            
            settlements.push_back(new Settlement(*settlement));


        }
        
        plans.clear();
        
        for (const Plan &otherPlan : other.plans)
        {
            string settlName = otherPlan.getSettlement().getName();
            Settlement *newSettelemnet = getSettlement(settlName);

            Plan newPlan = Plan(*newSettelemnet, otherPlan);
            plans.push_back(newPlan);
        }
        
        

        this->facilitiesOptions.clear();
        for (int i = 0; i < (int)other.facilitiesOptions.size(); i++)
        {
            this->facilitiesOptions.push_back(other.facilitiesOptions[i]);
        }
    }
    return *this;
}

Simulation::Simulation(const string &configFilePath) : isRunning(false), planCounter(0), actionsLog(), plans(), settlements(), facilitiesOptions()
{
    ifstream file(configFilePath);
    if (!file.is_open())
    {
        cerr << "Error opening config file" << endl;
        exit(-1);
    }

    string line;
    while (getline(file, line))
    {
        string data;

        vector<string> parsed = Auxiliary::parseArguments(line);
        data = parsed[0];

        if (data == "settlement")
        {
            // Parse settlement input
            string setName = parsed[1];
            int type = std::stoi(parsed[2]);

            SettlementType settlementType = static_cast<SettlementType>(type);
            settlements.push_back(new Settlement(setName, settlementType));
        }
        else if (data == "facility")
        {
            // Parse facility input
            string setName = parsed[1];
            int category = stoi(parsed[2]), price = stoi(parsed[3]), lifeQualityScore = stoi(parsed[4]), economyScore = stoi(parsed[5]), environmentScore = stoi(parsed[6]);

            FacilityCategory facilityCategory = static_cast<FacilityCategory>(category);
            facilitiesOptions.emplace_back(setName, facilityCategory, price, lifeQualityScore, economyScore, environmentScore);
        }
        else if (data == "plan")
        {
            // Parse plan input
            string settlementsetName = parsed[1];
            string policy = parsed[2];

            //  Find the settlement by setName
            Settlement *settlement = getSettlement(settlementsetName);
            if (!settlement)
            {
                cerr << "Error: Settlement not found for plan: " << settlementsetName << endl;
                exit(-1);
            }

            // Create a selection policy dynamically
            SelectionPolicy *selectionPolicy = nullptr;
            if (policy == "nve")
            {
                selectionPolicy = new NaiveSelection();
            }
            else if (policy == "bal")
            {
                selectionPolicy = new BalancedSelection(0, 0, 0);
            }
            else if (policy == "eco")
            {
                selectionPolicy = new EconomySelection();
            }
            else if (policy == "env")
            {
                selectionPolicy = new SustainabilitySelection();
            }
            else
            {
                cerr << "Error: Invalid selection policy: " << policy << endl;
                exit(-1);
            }


            // Add the plan to the plans vector
            plans.emplace_back(planCounter++, *settlement, selectionPolicy, facilitiesOptions);
        }
    }

    file.close();
}

void Simulation::start()
{
    // Mark simulation as running
    isRunning = true;

    // Output startup message
    std::cout << "Simulation has started" << std::endl;

    while (isRunning)
    {
        // Get a line of user input
        std::string line;
        std::getline(std::cin, line);

        // Skip empty lines
        if (line.empty())
        {
            continue;
        }

        // Handle commands
        vector<string> paresed = Auxiliary::parseArguments(line);
        string command = paresed[0];
        if (command == "plan")
        {
            string settlementsetName = paresed[1], policy = paresed[2];
            BaseAction *new_plan = new AddPlan(settlementsetName, policy);
            new_plan->act(*this);
            actionsLog.push_back(new_plan);
        }
        else if (command == "step")
        {
            int steps = stoi(paresed[1]);
            BaseAction *simuStep = new SimulateStep(steps);
            simuStep->act(*this);
            actionsLog.push_back(simuStep);
        }
        else if (command == "settlement")
        {
            string setName = paresed[1];
            SettlementType Set_type;
            if (paresed[2] == "0")
                Set_type = SettlementType::VILLAGE;
            if (paresed[2] == "1")
                Set_type = SettlementType::CITY;
            if (paresed[2] == "2")
                Set_type = SettlementType::METROPOLIS;
            BaseAction *newSettlement = new AddSettlement(setName, Set_type);
            newSettlement->act(*this);
            actionsLog.push_back(newSettlement);
        }
        else if (command == "facility")
        {
            string Facility_name = paresed[1];
            string catg = paresed[2];
            FacilityCategory facilityCategory;
            if (catg == "0")
                facilityCategory = FacilityCategory::LIFE_QUALITY;
            if (catg == "1")
                facilityCategory = FacilityCategory::ECONOMY;
            if (catg == "2")
                facilityCategory = FacilityCategory::ENVIRONMENT;

            int price = stoi(paresed[3]);
            int lifeQualityScore = stoi(paresed[4]);
            int economyScore = stoi(paresed[5]);
            int environmentScore = stoi(paresed[6]);
            BaseAction *addFac = new AddFacility(Facility_name, facilityCategory, price, lifeQualityScore, economyScore, environmentScore);
            addFac->act(*this);
            actionsLog.push_back(addFac);
        }
        else if (command == "planStatus")
        {

            int planId = stoi(paresed[1]);
            BaseAction *pstat = new PrintPlanStatus(planId);
            pstat->act(*this);
            actionsLog.push_back(pstat);
        }
        else if (command == "changePolicy")
        {
            int planId = stoi(paresed[1]);
            string newPolicy = paresed[2];
            BaseAction *changePol = new ChangePlanPolicy(planId, newPolicy);
            changePol->act(*this);
            actionsLog.push_back(changePol);
        }
        else if (command == "log")
        {
            BaseAction *log = new PrintActionsLog();
            log->act(*this);
            actionsLog.push_back(log);
        }
        else if (command == "close")
        {
            BaseAction *close = new Close();
            close->act(*this);
            actionsLog.push_back(close);
        }
        else if (command == "backup")
        {
            BaseAction *backup = new BackupSimulation();
            backup->act(*this);
            actionsLog.push_back(backup);
        }
        else if (command == "restore")
        {
            BaseAction *rest = new RestoreSimulation();
            rest->act(*this);
            actionsLog.push_back(rest);
        }

        
    }

    
}

void Simulation::addPlan(const Settlement &settlement, SelectionPolicy *selectionPolicy)
{
    plans.emplace_back(planCounter++, settlement, selectionPolicy, facilitiesOptions);
}

void Simulation::addAction(BaseAction *action)
{
    actionsLog.push_back(action);
}

bool Simulation::addSettlement(Settlement *settlement)
{
    if (isSettlementExists(settlement->getName()))
    {
        return false;
    }
    settlements.push_back(new Settlement(*settlement));
    return true;
}

bool Simulation::addFacility(FacilityType facility)
{
    for (const auto &existing : facilitiesOptions)
    {
        if (existing.getName() == facility.getName())
        {
            cout<<"return false to add facility"<<endl;
            return false;
        }
    }
    facilitiesOptions.push_back(facility);
    return true;
}

bool Simulation::isSettlementExists(const string &settlementsetName)
{
    return getSettlement(settlementsetName) != nullptr;
}

Settlement *Simulation::getSettlement(const string &settlementsetName)
{
    for (Settlement *settlement : settlements)
    {
        if (settlement->getName() == settlementsetName)
        {
            return settlement;
        }
    }
    return nullptr;
}

Plan &Simulation::getPlan(const int planID)
{
    for (Plan &plan : plans)
    {
        if (plan.getPlanID() == planID)
        {
            return plan;
        }
    }
    throw runtime_error("Plan doesn't exist");
}

void Simulation::step()
{
    for (int i = 0; i < (int)plans.size(); i++)
    {
        plans[i].step();
    }
}

void Simulation::close()
{
     isRunning = false; // end the runing
    
}

void Simulation::open()
{
    if (isRunning)
    {
        throw runtime_error("Simulation is already open and running.");
    }
    isRunning = true;
    cout << "Simulation is now open and ready for input." << endl;
}
const vector<BaseAction *> &Simulation::getActionsLog() const
{
    return actionsLog; // Return a reference to the vector of logged actions
}
const vector<Plan>& Simulation::getPlans() const {
    return plans;
}