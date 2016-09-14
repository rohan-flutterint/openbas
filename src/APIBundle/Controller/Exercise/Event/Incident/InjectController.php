<?php

namespace APIBundle\Controller\Exercise\Event\Incident;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use FOS\RestBundle\Controller\Annotations as Rest;
use Nelmio\ApiDocBundle\Annotation\ApiDoc;
use APIBundle\Entity\Exercise;
use APIBundle\Form\Type\InjectType;
use APIBundle\Entity\Event;
use APIBundle\Entity\Inject;

class InjectController extends Controller
{
    /**
     * @ApiDoc(
     *    description="List injects"
     * )
     *
     * @Rest\View(serializerGroups={"inject"})
     * @Rest\Get("/exercises/{exercise_id}/events/{event_id}/injects")
     */
    public function getExercisesEventsIncidentsInjectsAction(Request $request)
    {
        $em = $this->get('doctrine.orm.entity_manager');
        $exercise = $em->getRepository('APIBundle:Exercise')->find($request->get('exercise_id'));
        /* @var $exercise Exercise */

        if (empty($exercise)) {
            return $this->exerciseNotFound();
        }

        $this->denyAccessUnlessGranted('select', $exercise);

        $event = $em->getRepository('APIBundle:Event')->find($request->get('event_id'));
        /* @var $event Event */

        if (empty($event)) {
            return $this->eventNotFound();
        }


        $event = $em->getRepository('APIBundle:Incident')->find($request->get('incident_id'));
        /* @var $incident Incident */

        if (empty($incident)) {
            return $this->incidentNotFound();
        }

        $injects = $em->getRepository('APIBundle:Inject')->findBy(['inject_event' => $event]);

        return $injects;
    }

    /**
     * @ApiDoc(
     *    description="Create an inject",
     *    input={"class"=InjectType::class, "name"=""}
     * )
     *
     * @Rest\View(statusCode=Response::HTTP_CREATED, serializerGroups={"inject"})
     * @Rest\Post("/exercises/{exercise_id}/events/{event_id}/injects")
     */
    public function postExercisesEventsIncidentsInjectsAction(Request $request)
    {
        $em = $this->get('doctrine.orm.entity_manager');
        $exercise = $em->getRepository('APIBundle:Exercise')->find($request->get('exercise_id'));
        /* @var $exercise Exercise */

        if (empty($exercise)) {
            return $this->exerciseNotFound();
        }

        $this->denyAccessUnlessGranted('update', $exercise);

        $event = $em->getRepository('APIBundle:Event')->find($request->get('event_id'));
        /* @var $event Event */

        if (empty($event)) {
            return $this->eventNotFound();
        }

        $inject = new Inject();
        $inject->setInjectEvent($event);
        $form = $this->createForm(InjectType::class, $inject);
        $form->submit($request->request->all());

        if ($form->isValid()) {
            $em->persist($inject);
            $em->flush();
            return $inject;
        } else {
            return $form;
        }
    }

    /**
     * @ApiDoc(
     *    description="Delete an inject"
     * )
     *
     * @Rest\View(statusCode=Response::HTTP_NO_CONTENT, serializerGroups={"inject"})
     * @Rest\Delete("/exercises/{exercise_id}/events/{event_id}/injects/{inject_id}")
     */
    public function removeExercisesEventsIncidentsInjectAction(Request $request)
    {
        $em = $this->get('doctrine.orm.entity_manager');
        $exercise = $em->getRepository('APIBundle:Exercise')->find($request->get('exercise_id'));
        /* @var $exercise Exercise */

        if (empty($exercise)) {
            return $this->exerciseNotFound();
        }

        $this->denyAccessUnlessGranted('update', $exercise);

        $event = $em->getRepository('APIBundle:Event')->find($request->get('event_id'));
        /* @var $event Event */

        if (empty($event)) {
            return $this->eventNotFound();
        }

        $inject = $em->getRepository('APIBundle:Inject')->find($request->get('inject_id'));
        /* @var $inject Inject */

        if (empty($inject)) {
            return $this->injectNotFound();
        }

        $em->remove($inject);
        $em->flush();
    }

    /**
     * @ApiDoc(
     *    description="Replace an inject",
     *   input={"class"=InjectType::class, "name"=""}
     * )
     *
     * @Rest\View(serializerGroups={"inject"})
     * @Rest\Put("/exercises/{exercise_id}/events/{event_id}/injects/{inject_id}")
     */
    public function updateExercisesEventsInjectAction(Request $request)
    {
        return $this->updateInject($request, true);
    }

    /**
     * @ApiDoc(
     *    description="Update an inject",
     *    input={"class"=InjectType::class, "name"=""}
     * )
     *
     * @Rest\View(serializerGroups={"inject"})
     * @Rest\Patch("/exercises/{exercise_id}/events/{event_id}/injects/{inject_id}")
     */
    public function patchExercisesEventsIncidentsInjectAction(Request $request)
    {
        return $this->updateInject($request, false);
    }

    private function updateInject(Request $request, $clearMissing)
    {
        $em = $this->get('doctrine.orm.entity_manager');
        $exercise = $em->getRepository('APIBundle:Exercise')->find($request->get('exercise_id'));
        /* @var $exercise Exercise */

        if (empty($exercise)) {
            return $this->exerciseNotFound();
        }

        $this->denyAccessUnlessGranted('update', $exercise);

        $event = $em->getRepository('APIBundle:Event')->find($request->get('event_id'));
        /* @var $event Event */

        if (empty($event)) {
            return $this->eventNotFound();
        }

        $inject = $em->getRepository('APIBundle:Inject')->find($request->get('inject_id'));
        /* @var $inject Inject */

        if (empty($inject)) {
            return $this->injectNotFound();
        }

        $form = $this->createForm(InjectType::class, $inject);
        $form->submit($request->request->all(), $clearMissing);

        if ($form->isValid()) {
            $em->persist($inject);
            $em->flush();
            return $inject;
        } else {
            return $form;
        }
    }

    private function exerciseNotFound()
    {
        return \FOS\RestBundle\View\View::create(['message' => 'Exercise not found'], Response::HTTP_NOT_FOUND);
    }

    private function eventNotFound()
    {
        return \FOS\RestBundle\View\View::create(['message' => 'Event not found'], Response::HTTP_NOT_FOUND);
    }

    private function incidenttNotFound()
    {
        return \FOS\RestBundle\View\View::create(['message' => 'Incident not found'], Response::HTTP_NOT_FOUND);
    }

    private function injectNotFound()
    {
        return \FOS\RestBundle\View\View::create(['message' => 'Inject not found'], Response::HTTP_NOT_FOUND);
    }
}