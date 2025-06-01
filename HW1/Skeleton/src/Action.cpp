#include "Action.h"
#include "Settlement.h"
#include <iostream>
using namespace std;
    extern Simulation* backup; // External backup instance

BaseAction::BaseAction() : errorMsg(""), status(ActionStatus::COMPLETED)
{
}
ActionStatus BaseAction::getStatus() const
{
    return ActionStatus();
}

void BaseAction::complete()
{
    status = ActionStatus::COMPLETED;
}

void BaseAction::error(string errorMsg)
{
    this->errorMsg = errorMsg;
    status = ActionStatus::ERROR;
    std::cout << "Error: " << this->errorMsg << std::endl;
}

const string &BaseAction::getErrorMsg() const
{
    return errorMsg;
}

SimulateStep::SimulateStep(const int numOfSteps) : numOfSteps(numOfSteps)
{
}

void SimulateStep::act(Simulation &simulation)
{
    for (int i = 0; i < numOfSteps; i++)
    {
        simulation.step();
    }
    complete();
}

const string SimulateStep::toString() const
{
    return "step "+to_string(numOfSteps) +" COMPLETED";
}

SimulateStep *SimulateStep::clone() const
{
    return new SimulateStep(*this);
}

AddPlan::AddPlan(const string &settlementName, const string &selectionPolicy) : settlementName(settlementName), selectionPolicy(selectionPolicy)
{
}

void AddPlan::act(Simulation &simulation)
{
    if (!simulation.isSettlementExists(settlementName))
    {
        error("Cannot create this plan"); // settlementName not exist
        return;
    }
    if (selectionPolicy == "nve")
        simulation.addPlan(*simulation.getSettlement(settlementName), new NaiveSelection());
    else
    {
        if (selectionPolicy == "bal")
            simulation.addPlan(*simulation.getSettlement(settlementName), new BalancedSelection(0, 0, 0));
        else if (selectionPolicy == "eco")
        {
            simulation.addPlan(*simulation.getSettlement(settlementName), new EconomySelection());
        }
        else if (selectionPolicy == "env")
        {
            simulation.addPlan(*simulation.getSettlement(settlementName), new SustainabilitySelection());
        }
        else
            error("Cannot create this plan ");
    }
    complete();
}

const string AddPlan::toString() const
{
    return "plan " + settlementName + " " + selectionPolicy + " COMPLETED";
}

AddPlan *AddPlan::clone() const
{
    return new AddPlan(*this);
}

AddSettlement::AddSettlement(const string &settlementName, SettlementType settlementType)
    : settlementName(settlementName), settlementType(settlementType)
{
}

void AddSettlement::act(Simulation &simulation)
{
    if (simulation.isSettlementExists(settlementName))
        error("Settlement already exists");

    Settlement seT(settlementName, settlementType);
    simulation.addSettlement(&seT);
    complete();
}

AddSettlement *AddSettlement::clone() const
{
    return new AddSettlement(*this);
}

const string AddSettlement::toString() const
{
string typeStr;
    switch (settlementType) {
        case SettlementType::VILLAGE:
            typeStr = "0"; // Village corresponds to 0
            break;
        case SettlementType::CITY:
            typeStr = "1"; // City corresponds to 1
            break;
        case SettlementType::METROPOLIS:
            typeStr = "2"; // Metropolis corresponds to 2
            break;
    }
    return "settlement " + settlementName + " " + typeStr + " COMPLETED";}

AddFacility::AddFacility(const string &facilityName, const FacilityCategory facilityCategory, const int price, const int lifeQualityScore, const int economyScore, const int environmentScore)
    : facilityName(facilityName), facilityCategory(facilityCategory), price(price), lifeQualityScore(lifeQualityScore), economyScore(economyScore), environmentScore(environmentScore)
{
}

void AddFacility::act(Simulation &simulation)
{
    try
    {
        FacilityType fact(facilityName, facilityCategory, price, lifeQualityScore, economyScore, environmentScore);
        simulation.addFacility(fact);
        complete();
    }
    catch (...)
    {
        error("Facility already exists"); 
    }

}

AddFacility *AddFacility::clone() const
{
    return new AddFacility(*this);
}

const string AddFacility::toString() const
{
    string categoryStr ;
    switch (facilityCategory) {
        case FacilityCategory::LIFE_QUALITY:
            categoryStr = "0"; // Life Quality corresponds to 0
            break;
        case FacilityCategory::ECONOMY:
            categoryStr = "1"; // Economy corresponds to 1
            break;
        case FacilityCategory::ENVIRONMENT:
            categoryStr = "2"; // Environment corresponds to 2
            break;
    }
    return "facility " + facilityName + " "+ categoryStr +" " + std::to_string(price) + " " + std::to_string(lifeQualityScore) + " " + std::to_string(economyScore) + " " + std::to_string(environmentScore) + " COMPLETED";
}

PrintPlanStatus::PrintPlanStatus(int planId) : planId(planId)
{
}

void PrintPlanStatus::act(Simulation &simulation)
{
    try
    {
        Plan &plan = simulation.getPlan(planId); // Get the plan
        plan.printStatus();                      // Print its status 
        complete();                              // Mark the action as completed
    }
    catch (const std::exception &e)
    {
        error("Plan doesn't exist");
    }
}

PrintPlanStatus *PrintPlanStatus::clone() const
{
    return new PrintPlanStatus(*this);
}

const string PrintPlanStatus::toString() const
{
    if (getStatus() == ActionStatus::ERROR)
    {
        return "planStatus " + std::to_string(planId) + " ERROR: " + getErrorMsg();
    }
    return "planStatus " + std::to_string(planId) + " COMPLETED";
}

ChangePlanPolicy::ChangePlanPolicy(const int planId, const string &newPolicy) : planId(planId), newPolicy(newPolicy)
{
}

void ChangePlanPolicy::act(Simulation &simulation)
{
    try
    {
        Plan &plan = simulation.getPlan(planId); // Get the plan
        string previousPolicy = plan.getSelectionPolicy()->toString();

        // Check if the policy is the same
        if (previousPolicy == newPolicy)
        {
            error("Cannot change selection policy");
            return;
        }

        // Change the policy
        if (newPolicy == "nve")
        {
            plan.setSelectionPolicy(new NaiveSelection());
        }
        else if (newPolicy == "bal")
        {
            plan.setSelectionPolicy(new BalancedSelection(0, 0, 0));
        }
        else if (newPolicy == "eco")
        {
            plan.setSelectionPolicy(new EconomySelection());
        }
        else if (newPolicy == "env")
        {
            plan.setSelectionPolicy(new SustainabilitySelection());
        }
        else
        {
            error("Invalid selection policy");
            return;
        }

        complete(); // Mark as completed
    }
    catch (const exception &e)
    {
        error("Plan doesn't exist");
    }
}

ChangePlanPolicy *ChangePlanPolicy::clone() const
{
    return new ChangePlanPolicy(*this);
}

const string ChangePlanPolicy::toString() const
{
    if (getStatus() == ActionStatus::ERROR)
    {
        return "changePolicy " + to_string(planId) + " " + newPolicy + " ERROR: " + getErrorMsg();
    }
    return "changePolicy " + to_string(planId) + " " + newPolicy + " COMPLETED";
}

PrintActionsLog::PrintActionsLog()
{
}

void PrintActionsLog::act(Simulation &simulation)
{
    const vector<BaseAction *> &actionsLog = simulation.getActionsLog(); // Retrieve the log of actions

    for (const BaseAction *action : actionsLog)
    {
        // Use toString() method of each action to retrieve its formatted string and print it
        cout << action->toString() << endl;
    }

    complete(); // Mark the PrintActionsLog action as COMPLETE
}

PrintActionsLog *PrintActionsLog::clone() const
{
    return new PrintActionsLog(*this);
}

const string PrintActionsLog::toString() const
{
    return "log COMPLETED";
}

Close::Close()
{
}

void Close::act(Simulation &simulation)
{
     // Loop through all plans in the simulation
    for (const Plan &plan : simulation.getPlans()) {
        cout << "PlanID: " + to_string(plan.getPlanID()) << endl;
        cout << "SettlementName: " + plan.getSettlement().getName() << endl;
        cout << "LifeQualityScore: " + to_string(plan.getlifeQualityScore()) << endl;
        cout << "EconomyScore: " + to_string(plan.getEconomyScore()) << endl;
        cout << "EnvironmentScore: " + to_string(plan.getEnvironmentScore()) << endl;
    }
    // Close the simulation
    simulation.close();
    
    // Mark the action as complete
    complete();
}

Close *Close::clone() const
{
    return new Close(*this);
}

const string Close::toString() const
{
    return string();
}

BackupSimulation::BackupSimulation()
{
}

void BackupSimulation::act(Simulation &simulation)
{
    if (backup == nullptr){
         backup = new Simulation(simulation);
    }
    else 
    {
        if (backup == (&simulation))
        backup->operator=(simulation) ;
    }
    
    complete();
}

BackupSimulation *BackupSimulation::clone() const
{
    return new BackupSimulation(*this);
}

const string BackupSimulation::toString() const
{
    return "backup COMPLETED";
}

RestoreSimulation::RestoreSimulation()
{
}

void RestoreSimulation::act(Simulation& simulation)
{   
    if (backup == nullptr)
    {
        error("No backup available"); // Error if no backup exists
        return;
    }

    else
    
    {
        simulation = *backup;
        complete(); // Mark action as completed
    }
    
}


RestoreSimulation *RestoreSimulation::clone() const
{
    return new RestoreSimulation(*this);
}

const string RestoreSimulation::toString() const
{
    if (getStatus() == ActionStatus::ERROR)
    {
        return "restore ERROR: " + getErrorMsg();
    }
    return "restore COMPLETED";
}
///workspaces/Skeleton/src/Facility.cpp