#include "Plan.h"
#include <iostream>
#include <sstream>
#include <vector>
#include <algorithm>
#include <iostream>
using namespace std;
Plan::Plan(const Settlement &settlement, const Plan &other)
:Plan(other.plan_id, settlement, other.selectionPolicy->clone(),other.facilityOptions)
{
    status = other.status;
    life_quality_score = other.getlifeQualityScore();
    economy_score = other.getEconomyScore();
    environment_score = other.getEnvironmentScore();

    //Deep Copy of the Facility lists:
    for(Facility* facility : other.underConstruction){
       underConstruction.push_back(new Facility(*facility));
    }

    for(Facility* facility : other.facilities){
       facilities.push_back(new Facility(*facility));
    }
}
// TODO: check th constract settlement
Plan::Plan(const int planId, const Settlement &settlement, SelectionPolicy *selectionPolicy, const vector<FacilityType> &facilityOptions)
    : plan_id(planId),
      settlement(settlement),
      selectionPolicy(selectionPolicy),
      status(PlanStatus::AVALIABLE),
      facilities(), 
      underConstruction(),
      facilityOptions(facilityOptions), 
      life_quality_score(0),
      economy_score(0),
      environment_score(0)
      
{
}

// TODO: check if this deleted only one time
Plan::~Plan()
{
    delete selectionPolicy;
    for (Facility *f : facilities)
    {
        delete f;
    }
    for (Facility *unf : underConstruction)
    {
        delete unf;
    }
}

Plan::Plan(const Plan &other) : plan_id(other.plan_id),
                                settlement(other.settlement),
                                selectionPolicy(other.selectionPolicy->clone()), 
                                status(other.status),
                                facilities(), 
                                underConstruction(),
                                facilityOptions(other.facilityOptions),
                                life_quality_score(other.getlifeQualityScore()),
                                economy_score(other.getEconomyScore()),
                                environment_score(other.getEnvironmentScore())
                                 
{
    
    

    // Deep copy for facilities
    for (Facility *f : other.facilities)
    {
        facilities.push_back(new Facility(f->getName(), f->getSettlementName(), f->getCategory(), f->getTimeLeft(), f->getLifeQualityScore(), f->getEconomyScore(), f->getEnvironmentScore()));

    }

    // Deep copy for underConstruction
    for (Facility *f : other.underConstruction)
    {
        underConstruction.push_back(new Facility(*f));
    }
}

const int Plan::getlifeQualityScore() const
{
    return life_quality_score;
}

const int Plan::getEconomyScore() const
{
    return economy_score;
}

const int Plan::getEnvironmentScore() const
{
    return environment_score;
}

void Plan::setSelectionPolicy(SelectionPolicy *selectionPolicy)
{
    if (selectionPolicy)
        delete selectionPolicy;
    this->selectionPolicy = selectionPolicy;
}

void Plan::step()
{
    if (selectionPolicy)
    {
        // Add facilities if there is room for construction
        while ((int)underConstruction.size() < settlement.getConstructionLimit() && !facilityOptions.empty())
        {

            // Select the next facility to build
            FacilityType new_ToAddFacility = selectionPolicy->selectFacility(facilityOptions);

            // Create the facility and add it to underConstruction
            Facility *new_facility = new Facility(new_ToAddFacility, settlement.getName());
            underConstruction.push_back(new_facility);
            // Remove the selected facility from facilityOptions to avoid duplication
            // Remove the selected facility from the temporary copy
        }
        for (auto it = underConstruction.begin(); it != underConstruction.end();)
        {

            Facility *facility = *it;
            // do a step in facilty
            facility->step();

            // Check if the facility is now operational
            if (facility->getStatus() == FacilityStatus::OPERATIONAL)
            {
                addFacility(facility);
                it = underConstruction.erase(it); // remove the facility from underConstruction
            }
            else
            {
                ++it; // Move to the next facility
            }
        }

        

        // check the limit
        if ((int)underConstruction.size() == settlement.getConstructionLimit())
        {
            status = PlanStatus::BUSY;
        }
        else
        {

            status = PlanStatus::AVALIABLE; // Facilities can still be added
        }
    }
}
    
    void Plan::printStatus()
    {
        std::cout << "PlanID: " << plan_id << std::endl;
        std::cout << "Settlement Name: " << settlement.getName() << std::endl;
        std::cout << "Plan Status: " << (status == PlanStatus::AVALIABLE ? "AVAILABLE" : "BUSY") << std::endl;
        std::cout << "SelectionPolicy: : " << selectionPolicy->toString() << std::endl;
        std::cout << "Life Quality Score: " << life_quality_score << std::endl;
        std::cout << "Economy Score: " << economy_score << std::endl;
        std::cout << "Environment Score: " << environment_score << std::endl;

        for (int i = 0; i < (int)facilities.size(); i++)
        {
            std::cout << "FacilityName: " + facilities[i]->getName() << std::endl;
            std::cout << "FacilityStatus: OPERATIONAL" << std::endl;
        }
        for (int i = 0; i < (int)underConstruction.size(); i++)
        {
            std::cout << "FacilityName: " + underConstruction[i]->getName() << std::endl;
            std::cout << "FacilityStatus: UNDER_CONSTRUCTIONS" << std::endl;
        }
    }

    const vector<Facility *> &Plan::getFacilities() const
    {
        return facilities;
    }

    void Plan::addFacility(Facility * facility)
    {
        if (std::find(facilities.begin(), facilities.end(), facility) != facilities.end())
        {
            throw std::runtime_error("Facility already exists in the plan");
        }
        else
        {
            facilities.push_back(facility);
            life_quality_score += facility->getLifeQualityScore();
            environment_score += facility->getEnvironmentScore();
            economy_score += facility->getEconomyScore();
        }
    }

    const string Plan::toString() const
    {
        std::string result = "PlanID: " + std::to_string(plan_id) + "\n";

        result += "SettlementName: " + settlement.getName() + "\n";

        result += "LifeQualityScore: " + std::to_string(life_quality_score) + "\n";
        result += "EconomyScore: " + std::to_string(economy_score) + "\n";
        result += "EnvironmentScore: " + std::to_string(environment_score) + "\n";

        return result;
    }
    const vector<FacilityType> Plan::getfacilityOptions() const
    {
        return facilityOptions;
    }
    const SelectionPolicy *Plan::getSelectionPolicy() const
    {
        return selectionPolicy;
    }

    const int Plan::getPlanID() const
    {
        return plan_id;
    }

    const Settlement &Plan::getSettlement() const
    {
        return settlement;
    }
